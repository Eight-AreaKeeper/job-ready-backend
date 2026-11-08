package com.jobready.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table events: career fairs, workshops, networking nights and info sessions. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {

    private String id;
    private String title;
    /** Who runs the event. */
    private String host;
    /** career_fair | workshop | networking | info_session */
    private String kind;
    /** in_person | online */
    private String format;
    /** Address or city; "Online" for online events. */
    private String location;
    private String description;
    /** Start time, stored and returned in UTC. */
    private Instant startsAt;
    /** End time (UTC), may be null. */
    private Instant endsAt;
    /** Registration page. */
    private String url;
    @JsonIgnore
    private Instant createdAt;
}
