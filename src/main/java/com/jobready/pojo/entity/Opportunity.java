package com.jobready.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table opportunities. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Opportunity implements Serializable {

    private String id;
    private String title;
    private String organization;
    /** job | internship | research */
    private String type;
    private String industry;
    private String location;
    /** local | remote | abroad */
    private String scope;
    private String description;
    private List<String> requirements;
    private LocalDate deadline;
    private String url;
    @JsonIgnore
    private Instant createdAt;
}
