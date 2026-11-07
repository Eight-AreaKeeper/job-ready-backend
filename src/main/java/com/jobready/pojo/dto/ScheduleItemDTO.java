package com.jobready.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/** POST /schedule */
@Data
public class ScheduleItemDTO implements Serializable {

    @NotBlank
    private String opportunityId;
}
