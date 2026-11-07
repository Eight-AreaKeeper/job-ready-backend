package com.jobready.interviews;

import com.jobready.ai.AiModels.InterviewQuestion;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class InterviewDtos {

    private InterviewDtos() {
    }

    /** POST /interviews body: opportunityId OR role. */
    public record StartInterviewRequest(String opportunityId, String role) {
    }

    public record InterviewDto(String id, List<InterviewQuestion> questions) {
    }

    public record AnswerRequest(@NotBlank String questionId, @NotBlank String answer) {
    }

    public record AnswerFeedbackDto(int score, String feedback, InterviewQuestion nextQuestion) {
    }
}
