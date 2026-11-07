package com.jobready.mentors;

import com.jobready.mentors.Mentor.Slot;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class MentorDtos {

    private MentorDtos() {
    }

    /** POST /mentors body. */
    public record MentorSignupRequest(
            @NotBlank String headline,
            String company,
            List<String> industries,
            List<String> expertise,
            List<Slot> availability,
            @Min(0) Integer maxChatsPerWeek) {
    }

    /** Signup body plus id, name and points. */
    public record MentorDto(
            String id,
            String userId,
            String name,
            String headline,
            String company,
            List<String> industries,
            List<String> expertise,
            List<Slot> availability,
            Integer maxChatsPerWeek,
            int points) {

        public static MentorDto from(Mentor m, String name) {
            return new MentorDto(m.getId(), m.getUserId(), name, m.getHeadline(), m.getCompany(),
                    m.getIndustries(), m.getExpertise(), m.getAvailability(), m.getMaxChatsPerWeek(),
                    m.getPoints());
        }
    }
}
