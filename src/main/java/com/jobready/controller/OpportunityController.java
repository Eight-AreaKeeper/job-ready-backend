package com.jobready.controller;

import com.jobready.common.constant.StatusConstant;
import com.jobready.common.result.ListResult;
import com.jobready.pojo.dto.OpportunityPageQueryDTO;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.vo.OpportunityMatchVO;
import com.jobready.service.OpportunityService;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opportunities")
@Validated
@Slf4j
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    /** GET /opportunities?type=&scope=&industry=&q=&page=&limit= */
    @GetMapping
    public Result<ListResult<Opportunity>> page(
            @RequestParam(required = false) @Pattern(regexp = StatusConstant.OPPORTUNITY_TYPE_PATTERN) String type,
            @RequestParam(required = false) @Pattern(regexp = StatusConstant.SCOPE_PATTERN) String scope,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        OpportunityPageQueryDTO query = new OpportunityPageQueryDTO();
        query.setType(type);
        query.setScope(scope);
        query.setIndustry(industry);
        query.setQ(q);
        query.setPage(page);
        query.setLimit(limit);
        return Result.success(opportunityService.pageQuery(query));
    }

    /** GET /opportunities/matches?scope=&limit= */
    @GetMapping("/matches")
    public Result<ListResult<OpportunityMatchVO>> matches(
            @RequestParam(required = false) @Pattern(regexp = StatusConstant.SCOPE_PATTERN) String scope,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(opportunityService.matches(scope, limit));
    }

    @GetMapping("/{id}")
    public Result<Opportunity> getById(@PathVariable String id) {
        return Result.success(opportunityService.getById(id));
    }
}
