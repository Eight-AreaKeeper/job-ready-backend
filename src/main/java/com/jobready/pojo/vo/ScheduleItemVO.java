package com.jobready.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Schedule item joined with its opportunity's title and deadline. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItemVO implements Serializable {

    private String id;
    private String opportunityId;
    private String title;
    private LocalDate deadline;
    private String status;
}
