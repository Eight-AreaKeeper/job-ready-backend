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
import com.jobready.profile.ProfileAnswer;
import com.jobready.users.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Offline, deterministic stand-in for a real LLM. Good enough for front-end work and demos;
 * replace with a real provider once the team picks one (see {@link AiClient}).
 */
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "stub", matchIfMissing = true)
public class StubAiClient implements AiClient {

    @Override
    public ProfileDraft buildProfile(List<ProfileAnswer> answers, List<String> industries, String location) {
        // Treat the q1 answer ("skills you're confident in") as a comma-separated skill list.
        List<String> skills = answers.stream()
                .filter(a -> "q1".equals(a.getQuestionId()) && a.getAnswer() != null)
                .flatMap(a -> Arrays.stream(a.getAnswer().split("[,;]")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
        String summary = "Early-career candidate"
                + (skills.isEmpty() ? "" : " with skills in " + String.join(", ", skills))
                + (industries.isEmpty() ? "" : ", interested in " + String.join(", ", industries))
                + (location == null ? "." : ", based in " + location + ".");
        return new ProfileDraft(summary, skills, new ArrayList<>(industries), "student");
    }

    @Override
    public List<Match> matchOpportunities(Profile profile, List<Opportunity> candidates) {
        Set<String> mySkills = lower(profile.getSkills());
        Set<String> myIndustries = lower(profile.getIndustries());
        List<Match> out = new ArrayList<>();
        for (Opportunity o : candidates) {
            List<String> reasons = new ArrayList<>();
            List<String> hits = o.getRequirements().stream()
                    .filter(r -> mySkills.contains(r.toLowerCase(Locale.ROOT)))
                    .toList();
            double skillScore = o.getRequirements().isEmpty() ? 0.5
                    : (double) hits.size() / o.getRequirements().size();
            if (!hits.isEmpty()) {
                reasons.add("Matches your " + String.join(" and ", hits) + " skills");
            }
            double industryScore = 0;
            if (o.getIndustry() != null && myIndustries.contains(o.getIndustry().toLowerCase(Locale.ROOT))) {
                industryScore = 1;
                reasons.add("In your top industry: " + o.getIndustry());
            }
            double score = Math.round((0.7 * skillScore + 0.3 * industryScore) * 100) / 100.0;
            out.add(new Match(o, score, reasons));
        }
        return out;
    }

    @Override
    public Map<String, Object> generateResume(User user, Profile profile) {
        Map<String, Object> sections = new LinkedHashMap<>();
        sections.put("summary", profile.getSummary());
        sections.put("education", List.of());
        sections.put("experience", List.of());
        sections.put("projects", List.of());
        sections.put("skills", profile.getSkills());
        return sections;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TailorResult tailorResume(Map<String, Object> sections, Opportunity opportunity, String jobDescription) {
        Map<String, Object> copy = new LinkedHashMap<>(sections);
        List<String> changes = new ArrayList<>();
        if (opportunity != null) {
            Object raw = copy.getOrDefault("skills", List.of());
            Set<String> skills = new LinkedHashSet<>(raw instanceof List<?> l ? (List<String>) l : List.of());
            for (String req : opportunity.getRequirements()) {
                if (skills.stream().noneMatch(s -> s.equalsIgnoreCase(req))) {
                    changes.add("Consider adding " + req + " to skills if you have it");
                }
            }
            changes.add("Reordered skills to put the role's requirements first");
            List<String> reordered = new ArrayList<>(skills);
            Set<String> reqs = lower(opportunity.getRequirements());
            reordered.sort((a, b) -> Boolean.compare(!reqs.contains(a.toLowerCase(Locale.ROOT)),
                    !reqs.contains(b.toLowerCase(Locale.ROOT))));
            copy.put("skills", reordered);
        } else {
            changes.add("Stub AI: copied the base resume; plug in a real provider to tailor to pasted text");
        }
        return new TailorResult(copy, changes);
    }

    @Override
    public List<Suggestion> suggestImprovements(Map<String, Object> sections, Opportunity opportunity) {
        List<Suggestion> out = new ArrayList<>();
        if (opportunity != null && !opportunity.getRequirements().isEmpty()) {
            String req = opportunity.getRequirements().get(0);
            out.add(new Suggestion("experience", "Build a small project that uses " + req,
                    "Role asks for " + req));
        }
        out.add(new Suggestion("wording", "Quantify your results with numbers",
                "Recruiters skim for measurable impact"));
        return out;
    }

    @Override
    public String icebreaker(User student, Profile studentProfile, Mentor mentor, User mentorUser) {
        String first = mentorUser.getName() == null ? "there" : mentorUser.getName().split(" ")[0];
        String about = mentor.getHeadline() == null ? "your work" : "your work as a " + mentor.getHeadline();
        String interest = studentProfile == null || studentProfile.getIndustries().isEmpty() ? ""
                : " I'm exploring " + String.join(", ", studentProfile.getIndustries()) + ".";
        return "Hi " + first + ", I'm " + student.getName() + " and I came across " + about
                + (mentor.getCompany() == null ? "" : " at " + mentor.getCompany()) + "." + interest
                + " Would you have 20 minutes for a quick coffee chat?";
    }

    @Override
    public List<InterviewQuestion> interviewQuestions(String role, Opportunity opportunity) {
        List<InterviewQuestion> qs = new ArrayList<>();
        qs.add(new InterviewQuestion("iq1", "Tell me about yourself and why you want to be a " + role + "."));
        if (opportunity != null && !opportunity.getRequirements().isEmpty()) {
            qs.add(new InterviewQuestion("iq2",
                    "Tell me about a project where you used " + opportunity.getRequirements().get(0) + "."));
        } else {
            qs.add(new InterviewQuestion("iq2", "Tell me about a project you are proud of."));
        }
        qs.add(new InterviewQuestion("iq3", "Describe a time you worked through a disagreement on a team."));
        return qs;
    }

    @Override
    public AnswerFeedback gradeAnswer(String role, String question, String answer) {
        int words = answer.trim().split("\\s+").length;
        int score = Math.max(1, Math.min(10, words / 10 + 3));
        String feedback = words < 40
                ? "Good start. Add more detail: the situation, what you did, and the result with a number."
                : "Good structure. Make sure you end with the result and a number.";
        return new AnswerFeedback(score, feedback);
    }

    private static Set<String> lower(List<String> values) {
        return values == null ? Set.of()
                : values.stream().map(v -> v.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
}
