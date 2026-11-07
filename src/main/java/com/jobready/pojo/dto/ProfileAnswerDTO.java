package com.jobready.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One questionnaire answer. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileAnswerDTO implements Serializable {

    @NotBlank
    private String questionId;
    private String answer;
}
