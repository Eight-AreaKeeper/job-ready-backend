package com.jobready.pojo.dto;

import java.io.Serializable;
import lombok.Data;

/** POST /resumes/{id}/suggestions (opportunityId optional). */
@Data
public class ResumeSuggestionDTO implements Serializable {

    private String opportunityId;
}
