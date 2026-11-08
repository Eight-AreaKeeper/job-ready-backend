package com.jobready.pojo.entity;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table mentors. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mentor implements Serializable {

    private String id;
    private String userId;
    private String headline;
    private String company;
    private List<String> industries;
    private List<String> expertise;
    private List<AvailabilitySlot> availability;
    private Integer maxChatsPerWeek;
    private Integer points;
    /** Optional photo URL for the mentor card. */
    private String photoUrl;
    private Integer yearsExperience;
}
