package com.jobready.service.impl;

import com.jobready.common.constant.StatusConstant;
import com.jobready.common.context.BaseContext;
import com.jobready.common.exception.BaseException;
import com.jobready.common.utils.IdUtil;
import com.jobready.mapper.OpportunityMapper;
import com.jobready.mapper.ScheduleItemMapper;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.entity.ScheduleItem;
import com.jobready.pojo.vo.ScheduleItemVO;
import com.jobready.service.ScheduleService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleItemMapper scheduleItemMapper;
    private final OpportunityMapper opportunityMapper;

    /** Saved items sorted by deadline (no deadline last), optionally within [from, to]. */
    @Override
    public List<ScheduleItemVO> list(LocalDate from, LocalDate to) {
        List<ScheduleItem> mine = scheduleItemMapper.listByUserId(BaseContext.getCurrentId());
        if (mine.isEmpty()) {
            return List.of();
        }
        Map<String, Opportunity> byId = opportunityMapper
                .listByIds(mine.stream().map(ScheduleItem::getOpportunityId).toList()).stream()
                .collect(Collectors.toMap(Opportunity::getId, Function.identity()));
        return mine.stream()
                .map(i -> toVO(i, byId.get(i.getOpportunityId())))
                .filter(v -> from == null || (v.getDeadline() != null && !v.getDeadline().isBefore(from)))
                .filter(v -> to == null || (v.getDeadline() != null && !v.getDeadline().isAfter(to)))
                .sorted(Comparator.comparing(ScheduleItemVO::getDeadline,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Override
    @Transactional
    public ScheduleItemVO add(String opportunityId) {
        String userId = BaseContext.getCurrentId();
        Opportunity o = opportunityMapper.getById(opportunityId);
        if (o == null) {
            throw BaseException.notFound("opportunity");
        }
        if (scheduleItemMapper.countByUserAndOpportunity(userId, opportunityId) > 0) {
            throw BaseException.conflict("opportunity is already in your schedule");
        }
        ScheduleItem item = ScheduleItem.builder()
                .id(IdUtil.newId("s"))
                .userId(userId)
                .opportunityId(opportunityId)
                .status(StatusConstant.SCHEDULE_SAVED)
                .createdAt(Instant.now())
                .build();
        scheduleItemMapper.insert(item);
        return toVO(item, o);
    }

    @Override
    @Transactional
    public ScheduleItemVO updateStatus(String id, String status) {
        ScheduleItem item = owned(id);
        scheduleItemMapper.updateStatus(id, status);
        item.setStatus(status);
        return toVO(item, opportunityMapper.getById(item.getOpportunityId()));
    }

    @Override
    @Transactional
    public void delete(String id) {
        owned(id);
        scheduleItemMapper.deleteById(id);
    }

    private ScheduleItem owned(String id) {
        ScheduleItem item = scheduleItemMapper.getById(id);
        if (item == null || !item.getUserId().equals(BaseContext.getCurrentId())) {
            throw BaseException.notFound("schedule item");
        }
        return item;
    }

    private static ScheduleItemVO toVO(ScheduleItem i, Opportunity o) {
        return ScheduleItemVO.builder()
                .id(i.getId())
                .opportunityId(i.getOpportunityId())
                .title(o == null ? null : o.getTitle())
                .deadline(o == null ? null : o.getDeadline())
                .status(i.getStatus())
                .build();
    }
}
