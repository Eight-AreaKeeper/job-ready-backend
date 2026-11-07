package com.jobready.ai;

import com.jobready.opportunities.Opportunity;
import java.util.List;
import java.util.Map;

/** Plain data passed in and out of {@link AiClient}. */
public final class AiModels {

    private AiModels() {
    }

    public record ProfileDraft(String summary, List<String> skills, List<String> interests, String experienceLevel) {
    }

    public record Match(Opportunity opportunity, double score, List<String> reasons) {
    }

    public record TailorResult(Map<String, Object> sections, List<String> changes) {
    }

    /** type = experience | wording | skill */
    public record Suggestion(String type, String text, String reason) {
    }

    public record InterviewQuestion(String id, String text) {
    }

    public record AnswerFeedback(int score, String feedback) {
    }
}
