package com.jobready.pojo.vo;

import com.jobready.ai.AiModels.InterviewQuestion;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewVO implements Serializable {

    private String id;
    private List<InterviewQuestion> questions;
}
