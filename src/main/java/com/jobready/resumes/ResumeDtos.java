package com.jobready.resumes;

import com.jobready.ai.AiModels.Suggestion;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class ResumeDtos {

    private ResumeDtos() {
    }

    public record ResumeDto(String id, String title, String opportunityId, Map<String, Object> sections,
            Instant createdAt) {

        public static ResumeDto from(Resume r) {
            return new ResumeDto(r.getId(), r.getTitle(), r.getOpportunityId(), r.getSections(), r.getCreatedAt());
        }
    }

    /** POST /resumes/{id}/tailor body: send opportunityId OR jobDescription. */
    public record TailorRequest(String opportunityId, String jobDescription) {
    }

    public record TailorResponse(ResumeDto resume, List<String> changes) {
    }

    /** POST /resumes/{id}/suggestions body. */
    public record SuggestionsRequest(String opportunityId) {
    }

    public record SuggestionsResponse(List<Suggestion> suggestions) {
    }
}
