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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Table profiles: id, user_id, summary, skills, interests, industries, experience_level, location, updated_at. */
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

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> interests = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> industries = new ArrayList<>();

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
