package com.jobready.schedule;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.jobready.common.ListResponse;
import com.jobready.schedule.ScheduleDtos.CreateScheduleItemRequest;
import com.jobready.schedule.ScheduleDtos.ScheduleItemDto;
import com.jobready.schedule.ScheduleDtos.UpdateScheduleItemRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Schedule")
@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /** GET /schedule?from=&to= */
    @GetMapping
    public ListResponse<ScheduleItemDto> list(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ListResponse.of(scheduleService.list(userId, from, to));
    }

    /** POST /schedule -> 201 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleItemDto add(@AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateScheduleItemRequest req) {
        return scheduleService.add(userId, req.opportunityId());
    }

    /** PATCH /schedule/{id} */
    @PatchMapping("/{id}")
    public ScheduleItemDto update(@AuthenticationPrincipal String userId, @PathVariable String id,
            @Valid @RequestBody UpdateScheduleItemRequest req) {
        return scheduleService.updateStatus(userId, id, req.status());
    }

    /** DELETE /schedule/{id} -> 204 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal String userId, @PathVariable String id) {
        scheduleService.delete(userId, id);
    }
}
