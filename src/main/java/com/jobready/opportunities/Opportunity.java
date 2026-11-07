package com.jobready.opportunities;

import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Table opportunities: id, title, organization, type, industry, location, scope, description,
 * requirements, deadline, url, created_at.
 */
@Entity
@Table(name = "opportunities")
public class Opportunity {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String organization;

    /** job | internship | research */
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String industry;

    @Column(nullable = false)
    private String location;

    /** local | remote | abroad */
    @Column(nullable = false)
    private String scope;

    @Column(length = 8000)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> requirements = new ArrayList<>();

    private LocalDate deadline;

    @Column(length = 512)
    private String url;

    @Column(name = "created_at")
    private Instant createdAt;

    protected Opportunity() {
    }

    public Opportunity(String id, String title, String organization, String type, String industry, String location,
            String scope, String description, List<String> requirements, LocalDate deadline, String url) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.type = type;
        this.industry = industry;
        this.location = location;
        this.scope = scope;
        this.description = description;
        this.requirements = requirements == null ? new ArrayList<>() : requirements;
        this.deadline = deadline;
        this.url = url;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = Ids.newId("o");
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getType() { return type; }
    public String getIndustry() { return industry; }
    public String getLocation() { return location; }
    public String getScope() { return scope; }
    public String getDescription() { return description; }
    public List<String> getRequirements() { return requirements; }
    public LocalDate getDeadline() { return deadline; }
    public String getUrl() { return url; }
    public Instant getCreatedAt() { return createdAt; }
}
