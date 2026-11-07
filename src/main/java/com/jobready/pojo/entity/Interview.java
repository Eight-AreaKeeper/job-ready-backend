package com.jobready.pojo.entity;

import com.jobready.ai.AiModels.InterviewAnswer;
import com.jobready.ai.AiModels.InterviewQuestion;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table interviews (stretch goal). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Interview implements Serializable {

    private String id;
    private String userId;
    private String opportunityId;
    private String role;
    private List<InterviewQuestion> questions;
    private List<InterviewAnswer> answers;
    private Instant createdAt;
}
