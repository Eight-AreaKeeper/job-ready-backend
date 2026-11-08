package com.jobready.pojo.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.Data;

/** GET /events query. Only events that have not ended yet (relative to now) are returned. */
@Data
public class EventPageQueryDTO implements Serializable {

    private String kind;
    private String format;
    private String q;
    private int page = 1;
    private int limit = 20;
    /** Set by the service: events that ended before this time are left out. */
    private Instant now;

    /** Row offset for LIMIT/OFFSET. */
    public int getOffset() {
        return (Math.max(page, 1) - 1) * getPageSize();
    }

    /** limit clamped to 1..100. */
    public int getPageSize() {
        return Math.min(Math.max(limit, 1), 100);
    }
}
