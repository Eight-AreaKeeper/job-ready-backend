package com.jobready.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobready.ai.AiModels.AnswerFeedback;
import com.jobready.ai.AiModels.InterviewQuestion;
import com.jobready.ai.AiModels.Match;
import com.jobready.ai.AiModels.ProfileDraft;
import com.jobready.ai.AiModels.Suggestion;
import com.jobready.ai.AiModels.TailorResult;
import com.jobready.common.constant.ProfileQuestionConstant;
import com.jobready.common.exception.BaseException;
import com.jobready.common.properties.AppProperties;
import com.jobready.pojo.dto.ProfileAnswerDTO;
import com.jobready.pojo.entity.Mentor;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.entity.Profile;
import com.jobready.pojo.entity.User;
import com.jobready.pojo.vo.ProfileQuestionVO;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Real LLM provider: Google Gemini (free tier) through its OpenAI-compatible chat API.
 * Because the API is OpenAI-compatible, other free providers (Groq, OpenRouter, ...) also work
 * by changing app.ai.base-url / model / api-key.
 *
 * Every feature sends one system prompt (the rules + the JSON shape to answer with) and one user
 * message (the data as JSON), and parses the JSON object the model answers with.
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "gemini")
public class GeminiAiClient implements AiClient {

    private static final String PROFILE_PROMPT = """
            You are a career coach. Build a career profile for a student or early-career candidate from
            their questionnaire answers. Use ONLY what the answers say; never invent schools, employers,
            dates or skills. Split skills into short items ("Python", "SQL"). Leave a field empty when
            the answers say nothing about it.

            Reply with ONE JSON object and nothing else, in exactly this shape:
            {
              "summary": "2-3 sentence professional summary in resume style (no \"I\")",
              "skills": ["..."],
              "education": [{"school": "", "degree": "", "end": ""}],
              "projects": [{"name": "", "bullets": [""]}],
              "experience": [{"title": "", "org": "", "start": "", "end": "", "bullets": [""]}],
              "interests": ["career interests or fields"],
              "preferredWorkType": "short phrase or empty string",
              "experienceLevel": "beginner | student | intermediate | senior"
            }
            Write in English.
            """;

    private static final String MATCH_PROMPT = """
            You are a career matching assistant. Score how well each opportunity fits the candidate
            profile, from 0.0 (no fit) to 1.0 (perfect fit). Consider skills vs requirements, industries,
            experience level and location preferences. Give 1-3 short reasons per opportunity, written to
            the candidate ("Matches your Python skills"). Score every opportunity in the list, using its id.

            Reply with ONE JSON object and nothing else, in exactly this shape:
            {"matches": [{"id": "opportunity id", "score": 0.0, "reasons": ["..."]}]}
            Write in English.
            """;

    private static final String RESUME_PROMPT = """
            You are an expert resume writer. Write a general one-page resume from the candidate profile,
            using ONLY facts from the profile. Never invent employers, degrees, dates, projects or skills.
            You may reword, reorder and write strong bullet points (action verb + result) from the profile.

            Reply with ONE JSON object and nothing else, in exactly this shape:
            {
              "summary": "2-3 sentence summary",
              "skills": ["strongest skills first"],
              "education": [{"school": "", "degree": "", "end": ""}],
              "experience": [{"title": "", "org": "", "start": "", "end": "", "bullets": [""]}],
              "projects": [{"name": "", "bullets": [""]}]
            }
            Use empty arrays when the profile has nothing for a section. Write in English.
            """;

    private static final String SUGGEST_PROMPT = """
            You are a resume reviewer for students. Give 3-5 concrete, specific suggestions to improve the
            resume. If a target job is given, focus on closing the gap to that job. Each suggestion has a
            type: "experience" (something to go and do or build), "wording" (how to rewrite something),
            or "skill" (a skill to learn or show).

            Reply with ONE JSON object and nothing else, in exactly this shape:
            {"suggestions": [{"type": "experience | wording | skill", "text": "the suggestion", "reason": "why it helps"}]}
            Write in English.
            """;

    private static final String ICEBREAKER_PROMPT = """
            Write a short, friendly first message from a student to a mentor asking for a 20-minute
            coffee chat. Mention something specific about the mentor (headline, company or expertise) and
            one specific thing about the student (field, skill or goal). 60-100 words, no subject line,
            no placeholders in brackets, sign with the student's first name. Use only the facts given.

            Reply with ONE JSON object and nothing else, in exactly this shape:
            {"message": "..."}
            Write in English.
            """;

    private static final String INTERVIEW_PROMPT = """
            You are an interviewer. Write 5 mock interview questions for the role: 1 opener, 2 behavioural
            and 2 role-specific technical or situational questions. If a job posting is given, base the
            role-specific questions on its requirements. Questions should suit a student or new graduate.

            Reply with ONE JSON object and nothing else, in exactly this shape:
            {"questions": ["question 1", "question 2", "question 3", "question 4", "question 5"]}
            Write in English.
            """;

    private static final String GRADE_PROMPT = """
            You are an interview coach. Grade the candidate's answer to the interview question for the
            given role, from 1 (very weak) to 10 (excellent). Judge relevance, structure (situation, action,
            result), specifics and impact. Feedback: 2-4 sentences written directly to the candidate
            ("you", never "the candidate"), encouraging, with one concrete way to improve the answer.

            Reply with ONE JSON object and nothing else, in exactly this shape:
            {"score": 7, "feedback": "..."}
            Write in English.
            """;

    private static final String TAILOR_PROMPT = """
            You are an expert resume writer. Write a one-page resume tailored to the target job,
            using ONLY facts from the candidate profile. Never invent employers, degrees, dates,
            projects or skills the candidate does not have. You may reword, reorder, select the most
            relevant items and write strong bullet points (action verb + result) based on the profile.

            Reply with ONE JSON object and nothing else, in exactly this shape:
            {
              "summary": "2-3 sentence summary aimed at the target job",
              "skills": ["most relevant skills first"],
              "education": [{"school": "", "degree": "", "end": ""}],
              "experience": [{"title": "", "org": "", "start": "", "end": "", "bullets": [""]}],
              "projects": [{"name": "", "bullets": [""]}],
              "changes": ["short notes on what you tailored, and skills the job asks for that the candidate lacks"]
            }
            Use empty arrays when the profile has nothing for a section. Write in English.
            """;

    private final AppProperties.Ai props;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public GeminiAiClient(AppProperties appProperties, ObjectMapper objectMapper) {
        this.props = appProperties.ai();
        this.objectMapper = objectMapper;
        Duration timeout = Duration.ofSeconds(props.timeoutSeconds() > 0 ? props.timeoutSeconds() : 60);
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
        factory.setReadTimeout(timeout);
        this.restClient = RestClient.builder()
                .baseUrl(props.baseUrl())
                .requestFactory(factory)
                .defaultHeader("Authorization", "Bearer " + props.apiKey())
                .build();
        log.info("AI provider: {} (model {}){}", props.baseUrl(), props.model(),
                props.apiKey() == null || props.apiKey().isBlank() ? " - WARNING: app.ai.api-key is empty" : "");
    }

    @Override
    public ProfileDraft buildProfile(List<ProfileAnswerDTO> answers, List<String> industries, String location) {
        Map<String, String> questionText = ProfileQuestionConstant.QUESTIONS.stream()
                .collect(Collectors.toMap(ProfileQuestionVO::getId, ProfileQuestionVO::getText));
        List<Map<String, Object>> qa = new ArrayList<>();
        for (ProfileAnswerDTO a : answers) {
            if (a.getAnswer() == null || a.getAnswer().isBlank()) {
                continue;
            }
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("question", questionText.getOrDefault(a.getQuestionId(), a.getQuestionId()));
            m.put("answer", a.getAnswer().trim());
            qa.add(m);
        }
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("answers", qa);
        input.put("industriesOfInterest", industries);
        input.put("location", location);

        JsonNode reply = chatJson(PROFILE_PROMPT, toJson(input));
        String level = reply.path("experienceLevel").asText("").toLowerCase();
        if (!Set.of("beginner", "student", "intermediate", "senior").contains(level)) {
            level = "student";
        }
        String workType = reply.path("preferredWorkType").asText("");
        return new ProfileDraft(
                reply.path("summary").asText(""),
                convert(reply.path("skills"), new TypeReference<List<String>>() {}),
                convert(reply.path("education"), new TypeReference<List<Map<String, Object>>>() {}),
                convert(reply.path("projects"), new TypeReference<List<Map<String, Object>>>() {}),
                convert(reply.path("interests"), new TypeReference<List<String>>() {}),
                convert(reply.path("experience"), new TypeReference<List<Map<String, Object>>>() {}),
                workType.isBlank() ? null : workType,
                level);
    }

    @Override
    public List<Match> matchOpportunities(Profile profile, List<Opportunity> candidates) {
        if (candidates.isEmpty()) {
            return List.of();
        }
        List<Map<String, Object>> jobs = candidates.stream().map(o -> {
            Map<String, Object> m = jobForPrompt(o);
            m.put("id", o.getId());
            return m;
        }).toList();
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("profile", profileForPrompt(profile));
        input.put("opportunities", jobs);

        JsonNode reply = chatJson(MATCH_PROMPT, toJson(input));
        Map<String, JsonNode> byId = new HashMap<>();
        for (JsonNode m : reply.path("matches")) {
            byId.put(m.path("id").asText(), m);
        }
        List<Match> out = new ArrayList<>();
        for (Opportunity o : candidates) {
            JsonNode m = byId.get(o.getId());
            if (m == null) {
                // The model skipped this one: rank it last instead of failing the whole request.
                out.add(new Match(o, 0, List.of()));
                continue;
            }
            double score = Math.min(1, Math.max(0, m.path("score").asDouble(0)));
            out.add(new Match(o, Math.round(score * 100) / 100.0,
                    convert(m.path("reasons"), new TypeReference<List<String>>() {})));
        }
        return out;
    }

    @Override
    public Map<String, Object> generateResume(User user, Profile profile) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("candidateName", user.getName());
        input.put("profile", profileForPrompt(profile));
        return resumeSections(chatJson(RESUME_PROMPT, toJson(input)));
    }

    @Override
    public TailorResult tailorResume(User user, Profile profile, Opportunity opportunity, String jobDescription) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("candidateName", user.getName());
        input.put("profile", profileForPrompt(profile));
        if (opportunity != null) {
            input.put("targetJob", jobForPrompt(opportunity));
        } else {
            input.put("targetJobDescription", jobDescription);
        }
        JsonNode reply = chatJson(TAILOR_PROMPT, toJson(input));
        return new TailorResult(resumeSections(reply),
                convert(reply.path("changes"), new TypeReference<List<String>>() {}));
    }

    @Override
    public List<Suggestion> suggestImprovements(Map<String, Object> sections, Opportunity opportunity) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("resume", sections);
        if (opportunity != null) {
            input.put("targetJob", jobForPrompt(opportunity));
        }
        JsonNode reply = chatJson(SUGGEST_PROMPT, toJson(input));
        List<Suggestion> out = new ArrayList<>();
        for (JsonNode s : reply.path("suggestions")) {
            String type = s.path("type").asText("").toLowerCase();
            if (!Set.of("experience", "wording", "skill").contains(type)) {
                type = "wording";
            }
            String text = s.path("text").asText("");
            if (!text.isBlank()) {
                out.add(new Suggestion(type, text, s.path("reason").asText("")));
            }
        }
        return out;
    }

    @Override
    public String icebreaker(User student, Profile studentProfile, Mentor mentor, User mentorUser) {
        Map<String, Object> mentorInfo = new LinkedHashMap<>();
        mentorInfo.put("name", mentorUser.getName());
        mentorInfo.put("headline", mentor.getHeadline());
        mentorInfo.put("company", mentor.getCompany());
        mentorInfo.put("industries", mentor.getIndustries());
        mentorInfo.put("expertise", mentor.getExpertise());
        Map<String, Object> studentInfo = new LinkedHashMap<>();
        studentInfo.put("name", student.getName());
        if (studentProfile != null) {
            studentInfo.put("summary", studentProfile.getSummary());
            studentInfo.put("skills", studentProfile.getSkills());
            studentInfo.put("industries", studentProfile.getIndustries());
            studentInfo.put("interests", studentProfile.getInterests());
        }
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("mentor", mentorInfo);
        input.put("student", studentInfo);

        String message = chatJson(ICEBREAKER_PROMPT, toJson(input)).path("message").asText("");
        if (message.isBlank()) {
            throw BaseException.aiFailed("AI reply had no message");
        }
        return message.strip();
    }

    @Override
    public List<InterviewQuestion> interviewQuestions(String role, Opportunity opportunity) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("role", role);
        if (opportunity != null) {
            input.put("jobPosting", jobForPrompt(opportunity));
        }
        JsonNode reply = chatJson(INTERVIEW_PROMPT, toJson(input));
        List<InterviewQuestion> out = new ArrayList<>();
        for (JsonNode q : reply.path("questions")) {
            String text = q.isTextual() ? q.asText() : q.path("text").asText("");
            if (!text.isBlank()) {
                out.add(new InterviewQuestion("iq" + (out.size() + 1), text.strip()));
            }
        }
        if (out.isEmpty()) {
            throw BaseException.aiFailed("AI reply had no questions");
        }
        return out;
    }

    @Override
    public AnswerFeedback gradeAnswer(String role, String question, String answer) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("role", role);
        input.put("question", question);
        input.put("answer", answer);
        JsonNode reply = chatJson(GRADE_PROMPT, toJson(input));
        int score = Math.min(10, Math.max(1, reply.path("score").asInt(1)));
        return new AnswerFeedback(score, reply.path("feedback").asText(""));
    }

    /** The resume sections every resume endpoint stores. */
    private Map<String, Object> resumeSections(JsonNode reply) {
        Map<String, Object> sections = new LinkedHashMap<>();
        sections.put("summary", reply.path("summary").asText(""));
        sections.put("skills", convert(reply.path("skills"), new TypeReference<List<String>>() {}));
        sections.put("education", convert(reply.path("education"), new TypeReference<List<Map<String, Object>>>() {}));
        sections.put("experience", convert(reply.path("experience"), new TypeReference<List<Map<String, Object>>>() {}));
        sections.put("projects", convert(reply.path("projects"), new TypeReference<List<Map<String, Object>>>() {}));
        return sections;
    }

    /** The fields of an opportunity the model needs (description cut to keep prompts small). */
    private static Map<String, Object> jobForPrompt(Opportunity o) {
        Map<String, Object> job = new LinkedHashMap<>();
        job.put("title", o.getTitle());
        job.put("organization", o.getOrganization());
        job.put("type", o.getType());
        job.put("industry", o.getIndustry());
        job.put("location", o.getLocation());
        job.put("scope", o.getScope());
        String d = o.getDescription();
        job.put("description", d != null && d.length() > 1500 ? d.substring(0, 1500) + "..." : d);
        job.put("requirements", o.getRequirements());
        return job;
    }

    /** The fields of the profile that matter for a resume (no ids or timestamps). */
    private static Map<String, Object> profileForPrompt(Profile p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("summary", p.getSummary());
        m.put("skills", p.getSkills());
        m.put("education", p.getEducation());
        m.put("experience", p.getExperience());
        m.put("projects", p.getProjects());
        m.put("interests", p.getInterests());
        m.put("industries", p.getIndustries());
        m.put("experienceLevel", p.getExperienceLevel());
        m.put("preferredWorkType", p.getPreferredWorkType());
        m.put("location", p.getLocation());
        return m;
    }

    /** One chat completion that must answer with a JSON object. */
    private JsonNode chatJson(String systemPrompt, String userContent) {
        if (props.apiKey() == null || props.apiKey().isBlank()) {
            throw BaseException.aiFailed("AI api-key is not set (app.ai.api-key in application-dev.yml)");
        }
        String raw;
        try {
            raw = callWithRetry(props.model(), systemPrompt, userContent);
        } catch (RestClientException e) {
            boolean busy = e instanceof RestClientResponseException re && isBusy(re);
            boolean timedOut = e instanceof ResourceAccessException;
            String fallback = props.fallbackModel();
            if (!(busy || timedOut) || fallback == null || fallback.isBlank() || fallback.equals(props.model())) {
                throw toAiFailed(e);
            }
            log.warn("AI model {} {}, trying fallback model {}", props.model(),
                    busy ? "still busy" : "did not answer in time", fallback);
            try {
                raw = call(fallback, systemPrompt, userContent);
            } catch (RestClientException e2) {
                // e.g. the fallback model does not exist for this key: report the original error
                log.warn("AI fallback model {} failed too: {}", fallback, e2.getMessage());
                throw toAiFailed(e);
            }
        }
        try {
            String content = objectMapper.readTree(raw).path("choices").path(0).path("message").path("content").asText();
            return objectMapper.readTree(stripCodeFence(content));
        } catch (Exception e) {
            log.warn("AI reply was not valid JSON: {}", raw);
            throw BaseException.aiFailed("AI reply was not valid JSON");
        }
    }

    /** Calls the model, retrying with growing waits (2s, 4s, 8s...) while it answers 503 or 429. */
    private String callWithRetry(String model, String systemPrompt, String userContent) {
        int retries = Math.max(0, props.maxRetries());
        for (int attempt = 0; ; attempt++) {
            try {
                return call(model, systemPrompt, userContent);
            } catch (RestClientResponseException e) {
                if (!isBusy(e) || attempt >= retries) {
                    throw e;
                }
                long waitMs = 2000L << attempt;
                log.warn("AI model {} busy (HTTP {}), retry {}/{} in {} ms",
                        model, e.getStatusCode().value(), attempt + 1, retries, waitMs);
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }

    private String call(String model, String systemPrompt, String userContent) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userContent)));
        body.put("response_format", Map.of("type", "json_object"));
        if (props.reasoningEffort() != null && !props.reasoningEffort().isBlank()) {
            body.put("reasoning_effort", props.reasoningEffort());
        }
        return restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    /** 503 = model overloaded, 429 = rate limit: both usually pass after a short wait. */
    private static boolean isBusy(RestClientResponseException e) {
        int code = e.getStatusCode().value();
        return code == 503 || code == 429;
    }

    private static BaseException failed(RestClientResponseException e) {
        int code = e.getStatusCode().value();
        log.warn("AI call failed: HTTP {} {}", code, e.getResponseBodyAsString());
        String hint = code == 429 ? " (free-tier rate limit, try again in a minute)"
                : code == 503 ? " (model overloaded, try again later)" : "";
        return BaseException.aiFailed("AI service returned HTTP " + code + hint);
    }

    private static BaseException toAiFailed(RestClientException e) {
        if (e instanceof RestClientResponseException re) {
            return failed(re);
        }
        if (e instanceof ResourceAccessException && e.getCause() instanceof java.net.http.HttpTimeoutException) {
            log.warn("AI call timed out: {}", e.getMessage());
            return BaseException.aiFailed("AI service did not answer in time (model busy, try again)");
        }
        log.warn("AI call failed", e);
        return BaseException.aiFailed("AI service is unreachable");
    }

    /** Some models wrap JSON in ```json ... ``` even when asked not to. */
    private static String stripCodeFence(String s) {
        String t = s.strip();
        if (t.startsWith("```")) {
            t = t.substring(t.indexOf('\n') + 1);
            int end = t.lastIndexOf("```");
            if (end >= 0) {
                t = t.substring(0, end);
            }
        }
        return t.strip();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private <T> T convert(JsonNode node, TypeReference<T> type) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return objectMapper.convertValue(objectMapper.createArrayNode(), type);
        }
        return objectMapper.convertValue(node, type);
    }
}
