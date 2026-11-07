package com.jobready.service.impl;

import com.jobready.ai.AiClient;
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
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final ProfileService profileService;
    private final OpportunityService opportunityService;
    private final UserService userService;
    private final AiClient aiClient;

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

    /** AI writes a base resume from the career profile. */
    @Override
    @Transactional
    public Resume generate() {
        String userId = BaseContext.getCurrentId();
        Map<String, Object> sections = aiClient.generateResume(userService.getById(userId),
                profileService.getCurrent());
        return save(userId, null, "Base resume", sections);
    }

    /** Creates a NEW resume tailored to an opportunity or a pasted job description. */
    @Override
    @Transactional
    public ResumeTailorVO tailor(String id, ResumeTailorDTO dto) {
        boolean hasOpp = dto.getOpportunityId() != null && !dto.getOpportunityId().isBlank();
        boolean hasJd = dto.getJobDescription() != null && !dto.getJobDescription().isBlank();
        if (hasOpp == hasJd) {
            throw BaseException.badRequest("send exactly one of opportunityId or jobDescription");
        }
        Resume base = getById(id);
        Opportunity opp = hasOpp ? opportunityService.getById(dto.getOpportunityId()) : null;
        TailorResult result = aiClient.tailorResume(base.getSections(), opp, dto.getJobDescription());
        String title = "Tailored: " + (opp != null ? opp.getTitle() : "pasted job description");
        Resume saved = save(base.getUserId(), opp == null ? null : opp.getId(), title, result.sections());
        return new ResumeTailorVO(saved, result.changes());
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
