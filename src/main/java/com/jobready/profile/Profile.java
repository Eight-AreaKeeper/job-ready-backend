package com.jobready.profile;

import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Table profiles: id, user_id, summary, skills, education, projects, interests, experience,
 * industries, preferred_work_type, experience_level, location, updated_at.
 * education / projects / experience use the same item shapes as resume sections.
 */
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(length = 4000)
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> skills = new ArrayList<>();

    /** [{ school, degree, end }] */
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Map<String, Object>> education = new ArrayList<>();

    /** [{ name, bullets[] }] */
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Map<String, Object>> projects = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> interests = new ArrayList<>();

    /** [{ title, org, start, end, bullets[] }] */
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Map<String, Object>> experience = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> industries = new ArrayList<>();

    @Column(name = "preferred_work_type")
    private String preferredWorkType;

    /** beginner | student | intermediate | senior */
    @Column(name = "experience_level")
    private String experienceLevel;

    private String location;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected Profile() {
    }

    public Profile(String userId) {
        this.userId = userId;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        if (id == null) {
            id = Ids.newId("p");
        }
        updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public List<Map<String, Object>> getEducation() { return education; }
    public void setEducation(List<Map<String, Object>> education) { this.education = education; }
    public List<Map<String, Object>> getProjects() { return projects; }
    public void setProjects(List<Map<String, Object>> projects) { this.projects = projects; }
    public List<Map<String, Object>> getExperience() { return experience; }
    public void setExperience(List<Map<String, Object>> experience) { this.experience = experience; }
    public String getPreferredWorkType() { return preferredWorkType; }
    public void setPreferredWorkType(String preferredWorkType) { this.preferredWorkType = preferredWorkType; }
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public List<String> getIndustries() { return industries; }
    public void setIndustries(List<String> industries) { this.industries = industries; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Instant getUpdatedAt() { return updatedAt; }
}
