package com.jobready.opportunities;

import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Table opportunities: id, title, organization, type, industry, location, scope, description,
 * requirements, deadline, url.
 */
@Entity
@Table(name = "opportunities")
public class Opportunity {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    private String organization;

    /** job | internship | research */
    @Column(nullable = false)
    private String type;

    private String industry;

    private String location;

    /** local | remote | abroad */
    @Column(nullable = false)
    private String scope;

    @Column(length = 8000)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> requirements = new ArrayList<>();

    private LocalDate deadline;

    @Column(length = 2000)
    private String url;

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
}
