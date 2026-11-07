package com.jobready.pojo.dto;

import java.io.Serializable;
import lombok.Data;

/** GET /opportunities query. page starts at 1. */
@Data
public class OpportunityPageQueryDTO implements Serializable {

    private String type;
    private String scope;
    private String industry;
    private String q;
    private int page = 1;
    private int limit = 20;

    /** Row offset for LIMIT/OFFSET. */
    public int getOffset() {
        return (Math.max(page, 1) - 1) * getPageSize();
    }

    /** limit clamped to 1..100. */
    public int getPageSize() {
        return Math.min(Math.max(limit, 1), 100);
    }
}
