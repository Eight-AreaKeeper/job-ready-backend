package com.jobready.pojo.vo;

import com.jobready.pojo.entity.CoffeeChat;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A coffee chat plus the display names of both sides, so the front end needs no extra lookups. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeChatVO implements Serializable {

    private String id;
    private String studentId;
    private String studentName;
    /** mentors.id, not users.id. */
    private String mentorId;
    private String mentorName;
    private String status;
    private String message;
    private List<String> proposedTimes;
    private Instant scheduledAt;

    public static CoffeeChatVO from(CoffeeChat c, String studentName, String mentorName) {
        return new CoffeeChatVO(c.getId(), c.getStudentId(), studentName, c.getMentorId(), mentorName,
                c.getStatus(), c.getMessage(), c.getProposedTimes(), c.getScheduledAt());
    }
}
