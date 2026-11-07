package com.jobready.service;

import com.jobready.common.result.ListResult;
import com.jobready.pojo.dto.OpportunityPageQueryDTO;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.vo.OpportunityMatchVO;

public interface OpportunityService {

    ListResult<Opportunity> pageQuery(OpportunityPageQueryDTO query);

    ListResult<OpportunityMatchVO> matches(String scope, int limit);

    /** 404 if it doesn't exist. */
    Opportunity getById(String id);
}
