package com.jobready.service.impl;

import com.jobready.ai.AiClient;
import com.jobready.common.properties.AppProperties;
import com.jobready.common.utils.ResumePdfUtil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import com.jobready.pojo.entity.Profile;
import com.jobready.ai.AiModels.TailorResult;
import com.jobready.common.context.BaseContext;
import com.jobready.common.exception.BaseException;
import com.jobready.common.utils.IdUtil;
import com.jobready.mapper.ResumeMapper;
import com.jobready.pojo.dto.ResumeTailorDTO;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.entity.Resume;
import com.jobready.pojo.vo.ResumeSuggestionVO;
import com.jobready.pojo.vo.ResumeTailorVO;
import com.jobready.service.OpportunityService;
import com.jobready.service.ProfileService;
import com.jobready.service.ResumeService;
import com.jobready.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final ProfileService profileService;
    private final OpportunityService opportunityService;
    private final UserService userService;
    private final AiClient aiClient;
    private final ResumePdfUtil resumePdfUtil;
    private final AppProperties appProperties;

    @Override
    public List<Resume> list() {
        return resumeMapper.listByUserId(BaseContext.getCurrentId());
    }

    @Override
    public Resume getById(String id) {
        Resume r = resumeMapper.getById(id);
        if (r == null || !r.getUserId().equals(BaseContext.getCurrentId())) {
            throw BaseException.notFound("resume");
        }
        return r;
    }

    /** Title that marks the user's single base resume (see ResumeMapper.getBaseByUserId). */
    private static final String BASE_TITLE = "Base resume";

    /**
     * AI writes the base resume from the career profile. Each user has only one: generating again
     * overwrites it (same id) and drops its old PDF so the next download is redrawn.
     */
    // Not @Transactional: the AI call can take a while and must not hold a DB connection.
    @Override
    public Resume generate() {
        String userId = BaseContext.getCurrentId();
        Map<String, Object> sections = aiClient.generateResume(userService.getById(userId),
                profileService.getCurrent());
        Resume existing = resumeMapper.getBaseByUserId(userId);
        if (existing == null) {
            return save(userId, null, BASE_TITLE, sections);
        }
        existing.setSections(sections);
        existing.setCreatedAt(Instant.now());
        resumeMapper.updateSections(existing);
        try {
            Files.deleteIfExists(pdfFile(existing.getId()));
        } catch (IOException e) {
            log.warn("Could not delete old PDF of {}", existing.getId(), e);
        }
        return existing;
    }

    /**
     * AI writes a NEW resume from the user's profile, tailored to an opportunity
     * or a pasted job description.
     * Not @Transactional on purpose: the AI call can take a minute and must not hold a DB connection.
     */
    @Override
    public ResumeTailorVO tailor(ResumeTailorDTO dto) {
        boolean hasOpp = dto.getOpportunityId() != null && !dto.getOpportunityId().isBlank();
        boolean hasJd = dto.getJobDescription() != null && !dto.getJobDescription().isBlank();
        if (hasOpp == hasJd) {
            throw BaseException.badRequest("send exactly one of opportunityId or jobDescription");
        }
        String userId = BaseContext.getCurrentId();
        Profile profile = profileService.getCurrent();
        Opportunity opp = hasOpp ? opportunityService.getById(dto.getOpportunityId()) : null;
        TailorResult result = aiClient.tailorResume(userService.getById(userId), profile, opp,
                hasJd ? dto.getJobDescription() : null);
        String title = "Tailored: " + (opp != null ? opp.getTitle() : "pasted job description");
        Resume saved = save(userId, opp == null ? null : opp.getId(), title, result.sections());
        Path pdf = writePdf(saved);
        return new ResumeTailorVO(saved, result.changes(), pdf.toAbsolutePath().toString());
    }

    /** Returns the saved PDF file, creating it first if it is missing. */
    @Override
    public byte[] pdf(String id) {
        Resume resume = getById(id);
        Path file = pdfFile(resume.getId());
        try {
            if (!Files.exists(file)) {
                writePdf(resume);
            }
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UncheckedIOException("could not read " + file, e);
        }
    }

    /** Renders the resume and saves it as <pdf-dir>/<resumeId>.pdf. */
    private Path writePdf(Resume resume) {
        Path file = pdfFile(resume.getId());
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, resumePdfUtil.render(userService.getById(resume.getUserId()), resume));
            log.info("Saved resume PDF {}", file.toAbsolutePath());
            return file;
        } catch (IOException e) {
            throw new UncheckedIOException("could not write " + file, e);
        }
    }

    private Path pdfFile(String resumeId) {
        return Path.of(appProperties.resume().pdfDir()).resolve(resumeId + ".pdf");
    }

    @Override
    public ResumeSuggestionVO suggestions(String id, String opportunityId) {
        Resume resume = getById(id);
        Opportunity opp = opportunityId == null ? null : opportunityService.getById(opportunityId);
        return new ResumeSuggestionVO(aiClient.suggestImprovements(resume.getSections(), opp));
    }

    private Resume save(String userId, String opportunityId, String title, Map<String, Object> sections) {
        Resume resume = Resume.builder()
                .id(IdUtil.newId("r"))
                .userId(userId)
                .opportunityId(opportunityId)
                .title(title)
                .sections(sections)
                .createdAt(Instant.now())
                .build();
        resumeMapper.insert(resume);
        return resume;
    }
}
