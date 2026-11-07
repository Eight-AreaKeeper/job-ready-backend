package com.jobready.pojo.dto;

import com.jobready.pojo.entity.AvailabilitySlot;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/** POST /mentors */
@Data
public class MentorDTO implements Serializable {

    @NotBlank
    private String headline;
    private String company;
    private List<String> industries;
    private List<String> expertise;
    private List<AvailabilitySlot> availability;
    @Min(0)
    private Integer maxChatsPerWeek;
}
