package com.jobready.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.Map;

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

    public static final String EXPERIENCE_LEVELS = "beginner|student|intermediate|senior";

    /** PATCH /profile body. Every field is optional; only non-null fields change. */
    public record UpdateProfileRequest(
            String summary,
            List<String> skills,
            List<Map<String, Object>> education,
            List<Map<String, Object>> projects,
            List<String> interests,
            List<Map<String, Object>> experience,
            @Size(max = 3, message = "can have at most 3 entries") List<String> industries,
            String preferredWorkType,
            @Pattern(regexp = EXPERIENCE_LEVELS,
                    message = "must be beginner, student, intermediate or senior") String experienceLevel,
            String location) {
    }

    public record ProfileDto(
            String id,
            String userId,
            String summary,
            List<String> skills,
            List<Map<String, Object>> education,
            List<Map<String, Object>> projects,
            List<String> interests,
            List<Map<String, Object>> experience,
            List<String> industries,
            String preferredWorkType,
            String experienceLevel,
            String location,
            Instant updatedAt) {

        public static ProfileDto from(Profile p) {
            return new ProfileDto(p.getId(), p.getUserId(), p.getSummary(), p.getSkills(), p.getEducation(),
                    p.getProjects(), p.getInterests(), p.getExperience(), p.getIndustries(),
                    p.getPreferredWorkType(), p.getExperienceLevel(), p.getLocation(), p.getUpdatedAt());
        }
    }
}
