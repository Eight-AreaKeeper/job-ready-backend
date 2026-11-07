package com.jobready.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/** POST /coffee-chats/icebreaker */
@Data
public class IcebreakerDTO implements Serializable {

    @NotBlank
    private String mentorId;
}
