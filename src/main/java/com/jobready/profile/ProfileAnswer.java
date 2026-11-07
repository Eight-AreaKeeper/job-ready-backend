package com.jobready.profile;

import com.jobready.common.Ids;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/** Table profile_answers: id, user_id, question_id, answer. */
@Entity
@Table(name = "profile_answers")
public class ProfileAnswer {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(length = 4000)
    private String answer;

    protected ProfileAnswer() {
    }

    public ProfileAnswer(String userId, String questionId, String answer) {
        this.userId = userId;
        this.questionId = questionId;
        this.answer = answer;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = Ids.newId("pa");
        }
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getQuestionId() { return questionId; }
    public String getAnswer() { return answer; }
}
