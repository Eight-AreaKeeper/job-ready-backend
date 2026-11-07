package com.jobready.coffeechats;

import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Table coffee_chats: id, student_id, mentor_id, status, message, proposed_times, scheduled_at, created_at. */
@Entity
@Table(name = "coffee_chats")
public class CoffeeChat {

    @Id
    private String id;

    /** users.id of the student. */
    @Column(name = "student_id", nullable = false)
    private String studentId;

    /** mentors.id (not users.id). */
    @Column(name = "mentor_id", nullable = false)
    private String mentorId;

    /** pending | accepted | declined | completed */
    @Column(nullable = false)
    private String status = "pending";

    @Column(length = 4000)
    private String message;

    /** ISO 8601 strings with offset, kept as the student sent them. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "proposed_times")
    private List<String> proposedTimes = new ArrayList<>();

    /** Stored in UTC (hibernate.jdbc.time_zone). */
    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;

    @Column(name = "created_at")
    private Instant createdAt;

    protected CoffeeChat() {
    }

    public CoffeeChat(String studentId, String mentorId, String message, List<String> proposedTimes) {
        this.studentId = studentId;
        this.mentorId = mentorId;
        this.message = message;
        this.proposedTimes = proposedTimes == null ? new ArrayList<>() : proposedTimes;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = Ids.newId("c");
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getMentorId() { return mentorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public List<String> getProposedTimes() { return proposedTimes; }
    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
}
