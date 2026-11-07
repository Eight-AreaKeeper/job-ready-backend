package com.jobready.pojo.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table users. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private String id;
    private String name;
    private String email;
    private String passwordHash;
    /** student | mentor */
    private String role;
    private Instant createdAt;
}
