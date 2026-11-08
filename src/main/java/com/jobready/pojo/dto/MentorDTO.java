package com.jobready.pojo.dto;

import com.jobready.pojo.entity.AvailabilitySlot;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    /** Optional link to a photo (https URL). */
    @Size(max = 512)
    private String photoUrl;
    @Min(0)
    @Max(70)
    private Integer yearsExperience;
}
