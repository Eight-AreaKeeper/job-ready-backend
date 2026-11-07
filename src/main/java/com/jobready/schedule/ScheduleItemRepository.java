package com.jobready.schedule;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, String> {

    List<ScheduleItem> findByUserId(String userId);

    boolean existsByUserIdAndOpportunityId(String userId, String opportunityId);
}
