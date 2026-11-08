package com.jobready.controller;

import com.jobready.common.constant.StatusConstant;
import com.jobready.common.result.ListResult;
import com.jobready.common.result.Result;
import com.jobready.pojo.dto.EventPageQueryDTO;
import com.jobready.pojo.entity.Event;
import com.jobready.service.EventService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@Validated
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /** GET /events?kind=&format=&q=&page=&limit= : upcoming events, soonest first. */
    @GetMapping
    public Result<ListResult<Event>> page(
            @RequestParam(required = false) @Pattern(regexp = StatusConstant.EVENT_KIND_PATTERN) String kind,
            @RequestParam(required = false) @Pattern(regexp = StatusConstant.EVENT_FORMAT_PATTERN) String format,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        EventPageQueryDTO query = new EventPageQueryDTO();
        query.setKind(kind);
        query.setFormat(format);
        query.setQ(q);
        query.setPage(page);
        query.setLimit(limit);
        return Result.success(eventService.pageQuery(query));
    }

    @GetMapping("/{id}")
    public Result<Event> getById(@PathVariable String id) {
        return Result.success(eventService.getById(id));
    }
}
