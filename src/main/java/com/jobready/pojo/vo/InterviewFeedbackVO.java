package com.jobready.pojo.vo;

import com.jobready.ai.AiModels.InterviewQuestion;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewFeedbackVO implements Serializable {

    private int score;
    private String feedback;
    /** null after the last question. */
    private InterviewQuestion nextQuestion;
}
