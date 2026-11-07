package com.jobready.pojo.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table profiles. education / projects / experience use the same item shapes as resume sections. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile implements Serializable {

    private String id;
    private String userId;
    private String summary;
    private List<String> skills;
    /** [{ school, degree, end }] */
    private List<Map<String, Object>> education;
    /** [{ name, bullets[] }] */
    private List<Map<String, Object>> projects;
    private List<String> interests;
    /** [{ title, org, start, end, bullets[] }] */
    private List<Map<String, Object>> experience;
    private List<String> industries;
    private String preferredWorkType;
    /** beginner | student | intermediate | senior */
    private String experienceLevel;
    private String location;
    private Instant updatedAt;
}
