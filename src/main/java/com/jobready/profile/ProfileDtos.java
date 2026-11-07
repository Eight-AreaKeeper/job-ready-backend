package com.jobready.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class ProfileDtos {

    private ProfileDtos() {
    }

    public record AnswerInput(@NotBlank String questionId, String answer) {
    }

    /** POST /profile body. */
    public record SubmitProfileRequest(
            @NotNull @Valid List<AnswerInput> answers,
            @Size(max = 3, message = "can have at most 3 entries") List<String> industries,
            String location) {
    }

    /** PATCH /profile body. Every field is optional; only non-null fields change. */
    public record UpdateProfileRequest(
            String summary,
            List<String> skills,
            List<String> interests,
            @Size(max = 3, message = "can have at most 3 entries") List<String> industries,
            String experienceLevel,
            String location) {
    }

    public record ProfileDto(
            String id,
            String userId,
            String summary,
            List<String> skills,
            List<String> interests,
            List<String> industries,
            String experienceLevel,
            String location,
            Instant updatedAt) {

        public static ProfileDto from(Profile p) {
            return new ProfileDto(p.getId(), p.getUserId(), p.getSummary(), p.getSkills(), p.getInterests(),
                    p.getIndustries(), p.getExperienceLevel(), p.getLocation(), p.getUpdatedAt());
        }
    }
}
