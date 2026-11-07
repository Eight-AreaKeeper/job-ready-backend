package com.jobready.opportunities;

import com.jobready.common.ListResponse;
import com.jobready.opportunities.OpportunityDtos.MatchDto;
import com.jobready.opportunities.OpportunityDtos.OpportunityDto;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/opportunities")
public class OpportunitiesController {

    private final OpportunityService opportunityService;

    public OpportunitiesController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    /** GET /opportunities?type=&scope=&industry=&q=&page=&limit= */
    @GetMapping
    public ListResponse<OpportunityDto> list(
            @RequestParam(required = false) @Pattern(regexp = "job|internship|research") String type,
            @RequestParam(required = false) @Pattern(regexp = "local|remote|abroad") String scope,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return opportunityService.search(type, scope, industry, q, page, limit);
    }

    /** GET /opportunities/matches?scope=&limit= */
    @GetMapping("/matches")
    public ListResponse<MatchDto> matches(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) @Pattern(regexp = "local|remote|abroad") String scope,
            @RequestParam(defaultValue = "10") int limit) {
        return opportunityService.matches(userId, scope, limit);
    }

    /** GET /opportunities/{id} */
    @GetMapping("/{id}")
    public OpportunityDto get(@PathVariable String id) {
        return OpportunityDto.from(opportunityService.get(id));
    }
}
