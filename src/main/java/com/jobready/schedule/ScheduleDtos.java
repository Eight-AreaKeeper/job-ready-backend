package com.jobready.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public final class ScheduleDtos {

    private ScheduleDtos() {
    }

    public record CreateScheduleItemRequest(@NotBlank String opportunityId) {
    }

    public record UpdateScheduleItemRequest(
            @NotBlank @Pattern(regexp = ScheduleItem.STATUS_PATTERN,
                    message = "must be saved, applied, interview, offer or closed") String status) {
    }

    /** Schedule item joined with its opportunity's title and deadline. */
    public record ScheduleItemDto(String id, String opportunityId, String title, LocalDate deadline, String status) {
    }
}
