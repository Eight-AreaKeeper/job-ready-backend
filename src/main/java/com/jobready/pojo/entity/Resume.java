package com.jobready.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table resumes. sections = { summary, education[], experience[], projects[], skills[] }. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume implements Serializable {

    private String id;
    @JsonIgnore
    private String userId;
    /** Set when the resume was tailored to an opportunity. */
    private String opportunityId;
    private String title;
    private Map<String, Object> sections;
    private Instant createdAt;
}
