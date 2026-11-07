package com.jobready.opportunities;

import java.time.LocalDate;
import java.util.List;

public final class OpportunityDtos {

    private OpportunityDtos() {
    }

    public record OpportunityDto(
            String id,
            String title,
            String organization,
            String type,
            String industry,
            String location,
            String scope,
            String description,
            List<String> requirements,
            LocalDate deadline,
            String url) {

        public static OpportunityDto from(Opportunity o) {
            return new OpportunityDto(o.getId(), o.getTitle(), o.getOrganization(), o.getType(), o.getIndustry(),
                    o.getLocation(), o.getScope(), o.getDescription(), o.getRequirements(), o.getDeadline(),
                    o.getUrl());
        }
    }

    /** One item of GET /opportunities/matches. */
    public record MatchDto(OpportunityDto opportunity, double matchScore, List<String> reasons) {
    }
}
