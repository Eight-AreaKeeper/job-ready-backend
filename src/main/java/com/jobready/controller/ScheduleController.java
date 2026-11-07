package com.jobready.controller;

import com.jobready.common.result.ListResult;
import com.jobready.pojo.dto.ScheduleItemDTO;
import com.jobready.pojo.dto.ScheduleStatusDTO;
import com.jobready.pojo.vo.ScheduleItemVO;
import com.jobready.service.ScheduleService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedule")
@Slf4j
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /** GET /schedule?from=&to= */
    @GetMapping
    public Result<ListResult<ScheduleItemVO>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return Result.success(ListResult.of(scheduleService.list(from, to)));
    }

    @PostMapping
    public Result<ScheduleItemVO> add(@Valid @RequestBody ScheduleItemDTO dto) {
        log.info("Save opportunity to schedule: {}", dto.getOpportunityId());
        return Result.success(scheduleService.add(dto.getOpportunityId()));
    }

    @PatchMapping("/{id}")
    public Result<ScheduleItemVO> updateStatus(@PathVariable String id, @Valid @RequestBody ScheduleStatusDTO dto) {
        log.info("Update schedule item {} to {}", id, dto.getStatus());
        return Result.success(scheduleService.updateStatus(id, dto.getStatus()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        log.info("Delete schedule item {}", id);
        scheduleService.delete(id);
        return Result.success();
    }
}
