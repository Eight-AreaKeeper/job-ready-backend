package com.jobready.pojo.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table schedule_items. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItem implements Serializable {

    private String id;
    private String userId;
    private String opportunityId;
    /** saved | applied | interview | offer | closed */
    private String status;
    private Instant createdAt;
}
