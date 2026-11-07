package com.jobready.schedule;

import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/** Table schedule_items: id, user_id, opportunity_id, status, created_at. */
@Entity
@Table(name = "schedule_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "opportunity_id"}))
public class ScheduleItem {

    public static final String STATUS_PATTERN = "saved|applied|interview|offer|closed";

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "opportunity_id", nullable = false)
    private String opportunityId;

    /** saved | applied | interview | offer | closed */
    @Column(nullable = false)
    private String status = "saved";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ScheduleItem() {
    }

    public ScheduleItem(String userId, String opportunityId) {
        this.userId = userId;
        this.opportunityId = opportunityId;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = Ids.newId("s");
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getOpportunityId() { return opportunityId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}
