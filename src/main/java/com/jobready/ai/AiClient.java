package com.jobready.ai;

import com.jobready.ai.AiModels.AnswerFeedback;
import com.jobready.ai.AiModels.InterviewQuestion;
import com.jobready.ai.AiModels.Match;
import com.jobready.ai.AiModels.ProfileDraft;
import com.jobready.ai.AiModels.Suggestion;
import com.jobready.ai.AiModels.TailorResult;
import com.jobready.pojo.entity.Mentor;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.entity.Profile;
import com.jobready.pojo.dto.ProfileAnswerDTO;
import com.jobready.pojo.entity.User;
import java.util.List;
import java.util.Map;

/**
 * Every AI feature goes through this interface, so the provider can be swapped without
 * touching controllers or services.
 * <ul>
 *   <li>{@link StubAiClient} (app.ai.provider=stub, default): offline, rule-based results.</li>
 *   <li>{@link GeminiAiClient} (app.ai.provider=gemini): calls a real LLM for every feature.</li>
 * </ul>
 * Throw {@code BaseException.aiFailed(...)} when a provider call fails (maps to 502).
 */
public interface AiClient {

    /** POST /profile: turn questionnaire answers into a career profile. */
    ProfileDraft buildProfile(List<ProfileAnswerDTO> answers, List<String> industries, String location);

    /** GET /opportunities/matches: score each candidate against the profile (0..1) with reasons. */
    List<Match> matchOpportunities(Profile profile, List<Opportunity> candidates);

    /** POST /resumes/generate: base resume sections from the profile. */
    Map<String, Object> generateResume(User user, Profile profile);

    /**
     * POST /resumes/tailor: write resume sections for one job from the user's profile.
     * Exactly one of opportunity / jobDescription is non-null.
     */
    TailorResult tailorResume(User user, Profile profile, Opportunity opportunity, String jobDescription);

    /** POST /resumes/{id}/suggestions: opportunity may be null. */
    List<Suggestion> suggestImprovements(Map<String, Object> sections, Opportunity opportunity);

    /** POST /coffee-chats/icebreaker: studentProfile may be null. */
    String icebreaker(User student, Profile studentProfile, Mentor mentor, User mentorUser);

    /** POST /interviews (stretch): opportunity may be null. */
    List<InterviewQuestion> interviewQuestions(String role, Opportunity opportunity);

    /** POST /interviews/{id}/answers (stretch): score 1..10. */
    AnswerFeedback gradeAnswer(String role, String question, String answer);
}
