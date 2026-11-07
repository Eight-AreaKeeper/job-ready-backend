package com.jobready.service.impl;

import com.jobready.ai.AiClient;
import com.jobready.common.exception.BaseException;
import com.jobready.common.result.ListResult;
import com.jobready.mapper.OpportunityMapper;
import com.jobready.pojo.dto.OpportunityPageQueryDTO;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.entity.Profile;
import com.jobready.pojo.vo.OpportunityMatchVO;
import com.jobready.service.OpportunityService;
import com.jobready.service.ProfileService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityMapper opportunityMapper;
    private final ProfileService profileService;
    private final AiClient aiClient;

    @Override
    public ListResult<Opportunity> pageQuery(OpportunityPageQueryDTO query) {
        List<Opportunity> items = opportunityMapper.pageQuery(query);
        return new ListResult<>(items, opportunityMapper.countQuery(query));
    }

    @Override
    public ListResult<OpportunityMatchVO> matches(String scope, int limit) {
        Profile profile = profileService.getCurrent();
        List<OpportunityMatchVO> ranked = aiClient.matchOpportunities(profile, opportunityMapper.listByScope(scope))
                .stream()
                .map(m -> new OpportunityMatchVO(m.opportunity(), m.score(), m.reasons()))
                .sorted(Comparator.comparingDouble(OpportunityMatchVO::getMatchScore).reversed())
                .limit(Math.max(limit, 1))
                .toList();
        return ListResult.of(ranked);
    }

    @Override
    public Opportunity getById(String id) {
        Opportunity o = opportunityMapper.getById(id);
        if (o == null) {
            throw BaseException.notFound("opportunity");
        }
        return o;
    }
}
