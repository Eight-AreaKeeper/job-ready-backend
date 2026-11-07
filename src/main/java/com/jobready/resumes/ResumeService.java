package com.jobready.resumes;

import com.jobready.ai.AiClient;
import com.jobready.ai.AiModels.Suggestion;
import com.jobready.ai.AiModels.TailorResult;
import com.jobready.common.ApiException;
import com.jobready.opportunities.Opportunity;
import com.jobready.opportunities.OpportunityService;
import com.jobready.profile.Profile;
import com.jobready.profile.ProfileService;
import com.jobready.resumes.ResumeDtos.TailorRequest;
import com.jobready.users.User;
import com.jobready.users.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResumeService {

    private final ResumeRepository resumes;
    private final ProfileService profileService;
    private final OpportunityService opportunityService;
    private final UserService userService;
    private final AiClient ai;

    public ResumeService(ResumeRepository resumes, ProfileService profileService,
            OpportunityService opportunityService, UserService userService, AiClient ai) {
        this.resumes = resumes;
        this.profileService = profileService;
        this.opportunityService = opportunityService;
        this.userService = userService;
        this.ai = ai;
    }

    /** A newly saved tailored resume plus the AI's list of changes. */
    public record Tailored(Resume resume, List<String> changes) {
    }

    public List<Resume> list(String userId) {
        return resumes.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Resume get(String userId, String id) {
        return resumes.findById(id)
                .filter(r -> r.getUserId().equals(userId))
                .orElseThrow(() -> ApiException.notFound("resume"));
    }

    /** POST /resumes/generate: AI writes a base resume from the career profile. */
    @Transactional
    public Resume generate(String userId) {
        User user = userService.get(userId);
        Profile profile = profileService.get(userId);
        Map<String, Object> sections = ai.generateResume(user, profile);
        return resumes.save(new Resume(userId, null, "Base resume", sections));
    }

    /** POST /resumes/{id}/tailor: creates a NEW resume tailored to an opportunity or pasted job description. */
    @Transactional
    public Tailored tailor(String userId, String resumeId, TailorRequest req) {
        boolean hasOpp = req.opportunityId() != null && !req.opportunityId().isBlank();
        boolean hasJd = req.jobDescription() != null && !req.jobDescription().isBlank();
        if (hasOpp == hasJd) {
            throw ApiException.badRequest("send exactly one of opportunityId or jobDescription");
        }
        Resume base = get(userId, resumeId);
        Opportunity opp = hasOpp ? opportunityService.get(req.opportunityId()) : null;
        TailorResult result = ai.tailorResume(base.getSections(), opp, req.jobDescription());
        String title = "Tailored: " + (opp != null ? opp.getTitle() : "pasted job description");
        Resume saved = resumes.save(new Resume(userId, opp == null ? null : opp.getId(), title, result.sections()));
        return new Tailored(saved, result.changes());
    }

    /** POST /resumes/{id}/suggestions */
    public List<Suggestion> suggestions(String userId, String resumeId, String opportunityId) {
        Resume resume = get(userId, resumeId);
        Opportunity opp = opportunityId == null ? null : opportunityService.get(opportunityId);
        return ai.suggestImprovements(resume.getSections(), opp);
    }
}
