package com.jobready.mentors;

import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Table mentors: id, user_id, headline, company, industries, expertise, availability (JSON),
 * max_chats_per_week, points.
 */
@Entity
@Table(name = "mentors")
public class Mentor {

    /** One weekly slot, e.g. { "day": "Tue", "start": "18:00", "end": "19:00" }. */
    public record Slot(String day, String start, String end) {
    }

    @Id
    private String id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String headline;

    private String company;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> industries = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> expertise = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private List<Slot> availability = new ArrayList<>();

    @Column(name = "max_chats_per_week")
    private Integer maxChatsPerWeek = 2;

    @Column(nullable = false)
    private int points;

    protected Mentor() {
    }

    public Mentor(String userId) {
        this.userId = userId;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = Ids.newId("m");
        }
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public List<String> getIndustries() { return industries; }
    public void setIndustries(List<String> industries) { this.industries = industries; }
    public List<String> getExpertise() { return expertise; }
    public void setExpertise(List<String> expertise) { this.expertise = expertise; }
    public List<Slot> getAvailability() { return availability; }
    public void setAvailability(List<Slot> availability) { this.availability = availability; }
    public Integer getMaxChatsPerWeek() { return maxChatsPerWeek; }
    public void setMaxChatsPerWeek(Integer maxChatsPerWeek) { this.maxChatsPerWeek = maxChatsPerWeek; }
    public int getPoints() { return points; }

    public void addPoints(int amount) {
        this.points += amount;
    }
}
