package com.jobready.common.properties;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Settings under {@code app.*} in application.yml. */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Auth auth, Jwt jwt, Cors cors, Ai ai, Mentors mentors, ResumeFiles resume,
        TestData testData) {

    /**
     * mode = jwt  : every protected endpoint needs "Authorization: Bearer &lt;token&gt;".
     * mode = demo : requests without a token act as the demo user (front-end convenience).
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

    /**
     * provider = stub (default, rule-based, no network) | gemini (OpenAI-compatible chat API).
     * baseUrl / apiKey / model / timeoutSeconds are only used by the gemini provider.
     */
    public record Ai(String provider, String baseUrl, String apiKey, String model, String fallbackModel,
                     String reasoningEffort, int timeoutSeconds, int maxRetries) {
    }

    /** pdfDir = folder where generated resume PDFs are saved as &lt;resumeId&gt;.pdf. */
    public record ResumeFiles(String pdfDir) {
    }

    /**
     * Local development only: when enabled and the test users are missing, run sqlFile
     * (db/job_ready_db.sql) once on startup.
     */
    public record TestData(boolean enabled, String sqlFile) {
    }

    /** Points a mentor earns when a coffee chat is completed. */
    public record Mentors(int pointsPerCompletedChat) {
    }
}
