package com.jobready.schedule;

import com.jobready.common.ApiException;
import com.jobready.opportunities.Opportunity;
import com.jobready.opportunities.OpportunityRepository;
import com.jobready.schedule.ScheduleDtos.ScheduleItemDto;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleService {

    private final ScheduleItemRepository items;
    private final OpportunityRepository opportunities;

    public ScheduleService(ScheduleItemRepository items, OpportunityRepository opportunities) {
        this.items = items;
        this.opportunities = opportunities;
    }

    /** Saved items sorted by deadline (no deadline last), optionally within [from, to]. */
    public List<ScheduleItemDto> list(String userId, LocalDate from, LocalDate to) {
        List<ScheduleItem> mine = items.findByUserId(userId);
        Map<String, Opportunity> byId = opportunities
                .findAllById(mine.stream().map(ScheduleItem::getOpportunityId).toList()).stream()
                .collect(Collectors.toMap(Opportunity::getId, Function.identity()));
        return mine.stream()
                .map(i -> toDto(i, byId.get(i.getOpportunityId())))
                .filter(d -> from == null || (d.deadline() != null && !d.deadline().isBefore(from)))
                .filter(d -> to == null || (d.deadline() != null && !d.deadline().isAfter(to)))
                .sorted(Comparator.comparing(ScheduleItemDto::deadline,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Transactional
    public ScheduleItemDto add(String userId, String opportunityId) {
        Opportunity o = opportunities.findById(opportunityId)
                .orElseThrow(() -> ApiException.notFound("opportunity"));
        if (items.existsByUserIdAndOpportunityId(userId, opportunityId)) {
            throw ApiException.conflict("opportunity is already in your schedule");
        }
        return toDto(items.save(new ScheduleItem(userId, opportunityId)), o);
    }

    @Transactional
    public ScheduleItemDto updateStatus(String userId, String id, String status) {
        ScheduleItem item = owned(userId, id);
        item.setStatus(status);
        return toDto(items.save(item), opportunities.findById(item.getOpportunityId()).orElse(null));
    }

    @Transactional
    public void delete(String userId, String id) {
        items.delete(owned(userId, id));
    }

    private ScheduleItem owned(String userId, String id) {
        return items.findById(id)
                .filter(i -> i.getUserId().equals(userId))
                .orElseThrow(() -> ApiException.notFound("schedule item"));
    }

    private static ScheduleItemDto toDto(ScheduleItem i, Opportunity o) {
        return new ScheduleItemDto(i.getId(), i.getOpportunityId(), o == null ? null : o.getTitle(),
                o == null ? null : o.getDeadline(), i.getStatus());
    }
}
