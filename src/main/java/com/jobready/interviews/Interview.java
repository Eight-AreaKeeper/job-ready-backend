package com.jobready.interviews;

import com.jobready.ai.AiModels.InterviewAnswer;
import com.jobready.ai.AiModels.InterviewQuestion;
import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Mock interview (TBN, stretch goal).
 * Table interviews: id, user_id, opportunity_id, role, questions (JSON), answers (JSON), created_at.
 */
@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "opportunity_id")
    private String opportunityId;

    private String role;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<InterviewQuestion> questions = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private List<InterviewAnswer> answers = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Interview() {
    }

    public Interview(String userId, String opportunityId, String role, List<InterviewQuestion> questions) {
        this.userId = userId;
        this.opportunityId = opportunityId;
        this.role = role;
        this.questions = questions;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = Ids.newId("i");
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getOpportunityId() { return opportunityId; }
    public String getRole() { return role; }
    public List<InterviewQuestion> getQuestions() { return questions; }
    public List<InterviewAnswer> getAnswers() { return answers; }

    /** Records an answer, replacing any earlier answer to the same question. */
    public void recordAnswer(InterviewAnswer a) {
        List<InterviewAnswer> next = new ArrayList<>(answers == null ? List.of() : answers);
        next.removeIf(x -> x.questionId().equals(a.questionId()));
        next.add(a);
        this.answers = next;
    }

    public Instant getCreatedAt() { return createdAt; }
}
