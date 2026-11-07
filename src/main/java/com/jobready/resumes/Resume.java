package com.jobready.resumes;

import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Table resumes: id, user_id, opportunity_id, title, sections (JSON), created_at.
 * sections = { summary, education[], experience[], projects[], skills[] } as in the API docs.
 */
@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    /** Set when the resume was tailored to an opportunity. */
    @Column(name = "opportunity_id")
    private String opportunityId;

    @Column(nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(length = 100000)
    private Map<String, Object> sections = new LinkedHashMap<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Resume() {
    }

    public Resume(String userId, String opportunityId, String title, Map<String, Object> sections) {
        this.userId = userId;
        this.opportunityId = opportunityId;
        this.title = title;
        this.sections = sections;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = Ids.newId("r");
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getOpportunityId() { return opportunityId; }
    public String getTitle() { return title; }
    public Map<String, Object> getSections() { return sections; }
    public Instant getCreatedAt() { return createdAt; }
}
