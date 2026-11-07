package com.jobready.pojo.vo;

import com.jobready.pojo.entity.Opportunity;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One item of GET /opportunities/matches. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityMatchVO implements Serializable {

    private Opportunity opportunity;
    private double matchScore;
    private List<String> reasons;
}
