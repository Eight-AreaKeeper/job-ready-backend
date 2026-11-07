package com.jobready.pojo.dto;

import java.io.Serializable;
import lombok.Data;

/** POST /interviews: send opportunityId OR role. */
@Data
public class InterviewStartDTO implements Serializable {

    private String opportunityId;
    private String role;
}
