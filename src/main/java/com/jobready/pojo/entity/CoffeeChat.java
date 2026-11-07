package com.jobready.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table coffee_chats. mentorId is mentors.id, not users.id. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeChat implements Serializable {

    private String id;
    private String studentId;
    private String mentorId;
    /** pending | accepted | declined | completed */
    private String status;
    private String message;
    /** ISO 8601 strings with offset, kept as the student sent them. */
    private List<String> proposedTimes;
    /** Stored and returned in UTC. */
    private Instant scheduledAt;
    @JsonIgnore
    private Instant createdAt;
}
