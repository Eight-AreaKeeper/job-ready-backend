package com.jobready.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/** POST /interviews/{id}/answers */
@Data
public class InterviewAnswerDTO implements Serializable {

    @NotBlank
    private String questionId;
    @NotBlank
    private String answer;
}
