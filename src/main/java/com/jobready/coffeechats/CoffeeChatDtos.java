package com.jobready.coffeechats;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.List;

public final class CoffeeChatDtos {

    private CoffeeChatDtos() {
    }

    public record IcebreakerRequest(@NotBlank String mentorId) {
    }

    public record IcebreakerResponse(String message) {
    }

    public record CreateCoffeeChatRequest(@NotBlank String mentorId, String message, List<String> proposedTimes) {
    }

    public record UpdateCoffeeChatRequest(
            @NotBlank @Pattern(regexp = "accepted|declined|completed",
                    message = "must be accepted, declined or completed") String status,
            OffsetDateTime scheduledAt) {
    }

    public record CoffeeChatDto(String id, String studentId, String mentorId, String status, String message,
            List<String> proposedTimes, OffsetDateTime scheduledAt) {

        public static CoffeeChatDto from(CoffeeChat c) {
            return new CoffeeChatDto(c.getId(), c.getStudentId(), c.getMentorId(), c.getStatus(), c.getMessage(),
                    c.getProposedTimes(), c.getScheduledAt());
        }
    }
}
