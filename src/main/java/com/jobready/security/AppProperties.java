package com.jobready.security;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Settings under {@code app.*} in application.yml. */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Auth auth, Jwt jwt, Cors cors, Ai ai, Mentors mentors) {

    /**
     * mode = jwt  : every protected endpoint needs "Authorization: Bearer &lt;token&gt;".
     * mode = demo : requests without a token act as the demo user (handy for front-end work).
     */
    public record Auth(String mode, String demoUserEmail) {
        public boolean isDemo() {
            return "demo".equalsIgnoreCase(mode);
        }
    }

    public record Jwt(String secret, long expirationMinutes) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    /** provider = stub (default, no network). Add real providers in com.jobready.ai. */
    public record Ai(String provider) {
    }

    /** Points a mentor earns when a coffee chat is completed (rules still TBD). */
    public record Mentors(int pointsPerCompletedChat) {
    }
}
