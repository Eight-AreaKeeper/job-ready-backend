package com.jobready.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/** POST /coffee-chats */
@Data
public class CoffeeChatDTO implements Serializable {

    @NotBlank
    private String mentorId;
    private String message;
    private List<String> proposedTimes;
}
