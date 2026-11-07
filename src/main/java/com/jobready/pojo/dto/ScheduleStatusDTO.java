package com.jobready.pojo.dto;

import com.jobready.common.constant.StatusConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.Data;

/** PATCH /schedule/{id} */
@Data
public class ScheduleStatusDTO implements Serializable {

    @NotBlank
    @Pattern(regexp = StatusConstant.SCHEDULE_STATUS_PATTERN,
            message = "must be saved, applied, interview, offer or closed")
    private String status;
}
