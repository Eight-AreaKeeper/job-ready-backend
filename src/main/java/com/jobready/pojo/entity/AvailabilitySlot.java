package com.jobready.pojo.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One weekly mentor slot, e.g. { "day": "Tue", "start": "18:00", "end": "19:00" }. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySlot implements Serializable {

    private String day;
    private String start;
    private String end;
}
