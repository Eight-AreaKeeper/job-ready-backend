package com.jobready;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** End-to-end happy path through the main endpoints, using the in-memory H2 database and stub AI. */
@SpringBootTest
@AutoConfigureMockMvc
class ApiSmokeTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private String register(String name, String email, String role) throws Exception {
        String body = """
                {"name":"%s","email":"%s","password":"password123","role":"%s"}""".formatted(name, email, role);
        String res = mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.role").value(role))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + mapper.readTree(res).get("data").get("token").asText();
    }

    /** The "data" field of a Result response. */
    private JsonNode json(String s) throws Exception {
        return mapper.readTree(s).get("data");
    }

    @Test
    void protectedEndpointWithoutTokenReturns401ErrorShape() throws Exception {
        mvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void validationErrorUsesDocumentedShape() throws Exception {
        mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void studentFlow() throws Exception {
        String token = register("Paul", "paul@example.com", "student");

        mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"P\",\"email\":\"paul@example.com\",\"password\":\"password123\",\"role\":\"student\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("email is already registered"));

        mvc.perform(get("/api/v1/users/me").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("paul@example.com"));

        mvc.perform(get("/api/v1/profile").header("Authorization", token))
                .andExpect(status().isNotFound());

        mvc.perform(post("/api/v1/profile").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"answers":[{"questionId":"q1","answer":"Python, SQL"}],
                                 "industries":["Energy"],"location":"Calgary, AB"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.skills[0]").value("Python"))
                .andExpect(jsonPath("$.data.industries[0]").value("Energy"));

        mvc.perform(patch("/api/v1/profile").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"education":[{"school":"University of Calgary","degree":"BSc CS","end":"2029"}],
                                 "experienceLevel":"beginner"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.education[0].school").value("University of Calgary"))
                .andExpect(jsonPath("$.data.experienceLevel").value("beginner"));

        mvc.perform(get("/api/v1/opportunities?scope=local").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(2));

        mvc.perform(get("/api/v1/opportunities/matches").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].opportunity.id").value("o_seed_001"));

        String item = mvc.perform(post("/api/v1/schedule").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"opportunityId\":\"o_seed_001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("saved"))
                .andReturn().getResponse().getContentAsString();
        mvc.perform(patch("/api/v1/schedule/" + json(item).get("id").asText()).header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"applied\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("applied"));

        String resume = mvc.perform(post("/api/v1/resumes/generate").header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        mvc.perform(post("/api/v1/resumes/" + json(resume).get("id").asText() + "/tailor")
                        .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"opportunityId\":\"o_seed_001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.resume.opportunityId").value("o_seed_001"));

        mvc.perform(get("/api/v1/resumes/" + json(resume).get("id").asText()).header("Authorization", token))
                .andExpect(jsonPath("$.data.sections.education[0].school").value("University of Calgary"));

        String interview = mvc.perform(post("/api/v1/interviews").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"Data Analyst\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        mvc.perform(post("/api/v1/interviews/" + json(interview).get("id").asText() + "/answers")
                        .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":\"iq1\",\"answer\":\"I like data.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextQuestion.id").value("iq2"));
    }

    @Test
    void coffeeChatFlowAwardsMentorPoints() throws Exception {
        String mentor = register("Alex Kim", "alex@example.com", "mentor");
        String student = register("Ini", "ini@example.com", "student");

        String m = mvc.perform(post("/api/v1/mentors").header("Authorization", mentor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"headline\":\"Data Engineer\",\"industries\":[\"Energy\"]}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String mentorId = json(m).get("id").asText();

        mvc.perform(post("/api/v1/mentors").header("Authorization", student)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"headline\":\"x\"}"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/v1/coffee-chats/icebreaker").header("Authorization", student)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"mentorId\":\"" + mentorId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").isNotEmpty());

        String c = mvc.perform(post("/api/v1/coffee-chats").header("Authorization", student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mentorId\":\"" + mentorId + "\",\"message\":\"hi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("pending"))
                .andReturn().getResponse().getContentAsString();
        String chatId = json(c).get("id").asText();

        mvc.perform(patch("/api/v1/coffee-chats/" + chatId).header("Authorization", mentor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"accepted\",\"scheduledAt\":\"2026-11-12T18:00:00-07:00\"}"))
                .andExpect(status().isOk());
        mvc.perform(patch("/api/v1/coffee-chats/" + chatId).header("Authorization", student)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"completed\"}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/mentors").header("Authorization", student))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].points").value(10));
    }
}
