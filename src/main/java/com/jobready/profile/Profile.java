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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Table profiles: id, user_id, summary, skills, education, projects, interests, experience,
 * industries, preferred_work_type, experience_level, location, updated_at.
 * education / projects / experience use the same item shapes as resume sections.
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA needs a no-arg constructor
public class Profile {

    /** Generated in {@link #touch()}. */
    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    @Column(name = "user_id", nullable = false, unique = true)
    @Setter(AccessLevel.NONE)
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

    /** Set automatically on every save. */
    @Column(name = "updated_at")
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

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
}
