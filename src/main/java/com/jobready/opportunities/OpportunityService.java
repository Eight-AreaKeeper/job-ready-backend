package com.jobready.opportunities;

import com.jobready.ai.AiClient;
import com.jobready.common.ApiException;
import com.jobready.common.ListResponse;
import com.jobready.opportunities.OpportunityDtos.MatchDto;
import com.jobready.opportunities.OpportunityDtos.OpportunityDto;
import com.jobready.profile.Profile;
import com.jobready.profile.ProfileService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunities;
    private final ProfileService profileService;
    private final AiClient ai;

    public OpportunityService(OpportunityRepository opportunities, ProfileService profileService, AiClient ai) {
        this.opportunities = opportunities;
        this.profileService = profileService;
        this.ai = ai;
    }

    public Opportunity get(String id) {
        return opportunities.findById(id).orElseThrow(() -> ApiException.notFound("opportunity"));
    }

    /** GET /opportunities with optional filters; page is 1-based. */
    public ListResponse<OpportunityDto> search(String type, String scope, String industry, String q, int page,
            int limit) {
        Specification<Opportunity> spec = (root, query, cb) -> cb.conjunction();
        if (type != null) spec = spec.and((r, qy, cb) -> cb.equal(r.get("type"), type));
        if (scope != null) spec = spec.and((r, qy, cb) -> cb.equal(r.get("scope"), scope));
        if (industry != null) {
            spec = spec.and((r, qy, cb) -> cb.equal(cb.lower(r.get("industry")), industry.toLowerCase()));
        }
        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase() + "%";
            spec = spec.and((r, qy, cb) -> cb.or(
                    cb.like(cb.lower(r.get("title")), like),
                    cb.like(cb.lower(r.get("organization")), like),
                    cb.like(cb.lower(r.get("description")), like)));
        }
        PageRequest pageable = PageRequest.of(Math.max(page, 1) - 1, Math.min(Math.max(limit, 1), 100),
                Sort.by(Sort.Order.asc("deadline")));
        Page<Opportunity> result = opportunities.findAll(spec, pageable);
        return ListResponse.of(result.getContent().stream().map(OpportunityDto::from).toList(),
                result.getTotalElements());
    }

    /** GET /opportunities/matches: AI ranks opportunities against the user's profile. */
    public ListResponse<MatchDto> matches(String userId, String scope, int limit) {
        Profile profile = profileService.get(userId);
        List<Opportunity> candidates = scope == null
                ? opportunities.findAll()
                : opportunities.findAll((r, q, cb) -> cb.equal(r.get("scope"), scope));
        List<MatchDto> ranked = new ArrayList<>(ai.matchOpportunities(profile, candidates).stream()
                .map(m -> new MatchDto(OpportunityDto.from(m.opportunity()), m.score(), m.reasons()))
                .toList());
        ranked.sort(Comparator.comparingDouble(MatchDto::matchScore).reversed());
        return ListResponse.of(ranked.subList(0, Math.min(Math.max(limit, 1), ranked.size())));
    }
}
