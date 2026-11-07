package com.jobready.pojo.dto;

import com.jobready.common.constant.StatusConstant;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** PATCH /profile. Every field is optional; only non-null fields change. */
@Data
public class ProfileUpdateDTO implements Serializable {

    private String summary;
    private List<String> skills;
    private List<Map<String, Object>> education;
    private List<Map<String, Object>> projects;
    private List<String> interests;
    private List<Map<String, Object>> experience;
    @Size(max = 3, message = "can have at most 3 entries")
    private List<String> industries;
    private String preferredWorkType;
    @Pattern(regexp = StatusConstant.EXPERIENCE_LEVEL_PATTERN,
            message = "must be beginner, student, intermediate or senior")
    private String experienceLevel;
    private String location;
}
