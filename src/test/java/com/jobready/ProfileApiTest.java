package com.jobready;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Questionnaire, validation and field mapping for the career profile. */
@SpringBootTest
@AutoConfigureMockMvc
class ProfileApiTest {

    private static final String REQUIRED = """
            {"questionId":"q1","answer":"Python, SQL; pandas"},
            {"questionId":"q3","answer":"BSc Computer Science, University of Calgary, 2029"},
            {"questionId":"q4","answer":"student"},
            {"questionId":"q7","answer":"startup"}""";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    String token;

    @BeforeEach
    void register() throws Exception {
        String email = "p" + UUID.randomUUID() + "@example.com";
        String res = mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ini\",\"email\":\"" + email
                                + "\",\"password\":\"password123\",\"role\":\"student\"}"))
                .andReturn().getResponse().getContentAsString();
        token = "Bearer " + mapper.readTree(res).get("token").asText();
    }

    private ResultActions submit(String answers, String extra) throws Exception {
        return mvc.perform(post("/api/v1/profile").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"answers\":[" + answers + "]" + extra + "}"));
    }

    @Test
    void questionnaireListsRequiredFlagsAndOptions() throws Exception {
        mvc.perform(get("/api/v1/profile/questions").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(7))
                .andExpect(jsonPath("$.items[0].required").value(true))
                .andExpect(jsonPath("$.items[3].options[0]").value("Beginner"))
                .andExpect(jsonPath("$.items[0].field").doesNotExist());
    }

    @Test
    void answersFillTheMatchingProfileFields() throws Exception {
        submit(REQUIRED + """
                ,{"questionId":"q2","answer":"AI, energy"},
                {"questionId":"q5","answer":"IT assistant at UCalgary, summer 2025"},
                {"questionId":"q6","answer":"Built a crop yield model in PyTorch"}""",
                ",\"industries\":[\" Energy \",\"energy\",\"Software\"],\"location\":\"Calgary, AB\"")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.skills.length()").value(3))
                .andExpect(jsonPath("$.skills[2]").value("pandas"))
                .andExpect(jsonPath("$.interests[1]").value("energy"))
                .andExpect(jsonPath("$.education[0].school")
                        .value("BSc Computer Science, University of Calgary, 2029"))
                .andExpect(jsonPath("$.experience[0].bullets[0]").value("IT assistant at UCalgary, summer 2025"))
                .andExpect(jsonPath("$.projects[0].bullets[0]").value("Built a crop yield model in PyTorch"))
                .andExpect(jsonPath("$.experienceLevel").value("student"))
                .andExpect(jsonPath("$.preferredWorkType").value("Startup"))
                .andExpect(jsonPath("$.industries.length()").value(2))
                .andExpect(jsonPath("$.location").value("Calgary, AB"));

        mvc.perform(get("/api/v1/profile").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferredWorkType").value("Startup"));
    }

    @Test
    void submittingAgainReplacesTheProfile() throws Exception {
        submit(REQUIRED + ",{\"questionId\":\"q6\",\"answer\":\"Old project\"}", "").andExpect(status().isCreated());
        submit(REQUIRED, "")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projects.length()").value(0));
    }

    @Test
    void missingRequiredAnswerIs400() throws Exception {
        submit("{\"questionId\":\"q1\",\"answer\":\"Python\"}", "")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("q3 is required"));
    }

    @Test
    void blankRequiredAnswerIs400() throws Exception {
        submit(REQUIRED.replace("\"Python, SQL; pandas\"", "\"   \""), "")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("q1 is required"));
    }

    @Test
    void unknownQuestionIs400() throws Exception {
        submit(REQUIRED + ",{\"questionId\":\"q99\",\"answer\":\"x\"}", "")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("unknown questionId q99"));
    }

    @Test
    void duplicateQuestionIs400() throws Exception {
        submit(REQUIRED + ",{\"questionId\":\"q1\",\"answer\":\"Java\"}", "")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("q1 is answered more than once"));
    }

    @Test
    void optionOutsideTheListIs400() throws Exception {
        submit(REQUIRED.replace("\"startup\"", "\"Spaceship\""), "")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message")
                        .value("q7 must be one of Startup, Large company, Research lab, Non-profit, Government"));
    }

    @Test
    void tooManyIndustriesIs400() throws Exception {
        submit(REQUIRED, ",\"industries\":[\"A\",\"B\",\"C\",\"D\"]").andExpect(status().isBadRequest());
    }

    @Test
    void patchValidatesExperienceLevelAndProfileMustExist() throws Exception {
        mvc.perform(patch("/api/v1/profile").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"summary\":\"x\"}"))
                .andExpect(status().isNotFound());
        submit(REQUIRED, "").andExpect(status().isCreated());
        mvc.perform(patch("/api/v1/profile").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"experienceLevel\":\"expert\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/v1/profile").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"preferredWorkType\":\"Research lab\",\"skills\":[\"Go\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferredWorkType").value("Research lab"))
                .andExpect(jsonPath("$.skills[0]").value("Go"))
                .andExpect(jsonPath("$.experienceLevel").value("student"));
    }
}
