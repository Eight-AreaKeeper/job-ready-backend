package com.jobready.pojo.dto;

import java.io.Serializable;
import lombok.Data;

/** POST /resumes/tailor: send opportunityId OR jobDescription. */
@Data
public class ResumeTailorDTO implements Serializable {

    private String opportunityId;
    private String jobDescription;
}
