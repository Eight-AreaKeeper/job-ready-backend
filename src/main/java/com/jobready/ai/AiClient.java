package com.jobready.ai;

import com.jobready.ai.AiModels.AnswerFeedback;
import com.jobready.ai.AiModels.InterviewQuestion;
import com.jobready.ai.AiModels.Match;
import com.jobready.ai.AiModels.ProfileDraft;
import com.jobready.ai.AiModels.Suggestion;
import com.jobready.ai.AiModels.TailorResult;
import com.jobready.mentors.Mentor;
import com.jobready.opportunities.Opportunity;
import com.jobready.profile.Profile;
import com.jobready.profile.ProfileDtos.AnswerInput;
import com.jobready.users.User;
import java.util.List;
import java.util.Map;

/**
 * Every AI feature goes through this interface, so the provider can be swapped without
 * touching controllers or services. The AI provider is NOT decided yet:
 * {@link StubAiClient} is the default (app.ai.provider=stub) and returns rule-based results.
 *
 * To add a real provider, create another implementation annotated with
 * {@code @ConditionalOnProperty(name = "app.ai.provider", havingValue = "<name>")}
 * and throw {@code ApiException.aiFailed(...)} when the provider call fails (maps to 502).
 */
public interface AiClient {

    /** POST /profile: turn questionnaire answers into a career profile. */
    ProfileDraft buildProfile(List<AnswerInput> answers, List<String> industries, String location);

    /** GET /opportunities/matches: score each candidate against the profile (0..1) with reasons. */
    List<Match> matchOpportunities(Profile profile, List<Opportunity> candidates);

    /** POST /resumes/generate: base resume sections from the profile. */
    Map<String, Object> generateResume(User user, Profile profile);

    /** POST /resumes/{id}/tailor: opportunity OR jobDescription is non-null. */
    TailorResult tailorResume(Map<String, Object> sections, Opportunity opportunity, String jobDescription);

    /** POST /resumes/{id}/suggestions: opportunity may be null. */
    List<Suggestion> suggestImprovements(Map<String, Object> sections, Opportunity opportunity);

    /** POST /coffee-chats/icebreaker: studentProfile may be null. */
    String icebreaker(User student, Profile studentProfile, Mentor mentor, User mentorUser);

    /** POST /interviews (stretch): opportunity may be null. */
    List<InterviewQuestion> interviewQuestions(String role, Opportunity opportunity);

    /** POST /interviews/{id}/answers (stretch): score 1..10. */
    AnswerFeedback gradeAnswer(String role, String question, String answer);
}
