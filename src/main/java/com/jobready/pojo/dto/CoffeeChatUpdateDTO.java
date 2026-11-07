package com.jobready.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Data;

/** PATCH /coffee-chats/{id} */
@Data
public class CoffeeChatUpdateDTO implements Serializable {

    @NotBlank
    @Pattern(regexp = "accepted|declined|completed", message = "must be accepted, declined or completed")
    private String status;
    private OffsetDateTime scheduledAt;
}
