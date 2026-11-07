package com.jobready.profile;

import com.jobready.ai.AiClient;
import com.jobready.ai.AiModels.ProfileDraft;
import com.jobready.common.ApiException;
import com.jobready.profile.ProfileDtos.SubmitProfileRequest;
import com.jobready.profile.ProfileDtos.UpdateProfileRequest;
import com.jobready.profile.ProfileQuestions.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profiles;
    private final AiClient ai;

    public ProfileService(ProfileRepository profiles, AiClient ai) {
        this.profiles = profiles;
        this.ai = ai;
    }

    public Optional<Profile> find(String userId) {
        return profiles.findByUserId(userId);
    }

    public Profile get(String userId) {
        return find(userId).orElseThrow(() -> ApiException.notFound("profile"));
    }

    /**
     * Sends the answers to the AI and replaces any existing profile.
     * Answers are not stored (the profile_answers table was dropped from the schema).
     */
    @Transactional
    public Profile submit(String userId, SubmitProfileRequest req) {
        Map<Field, String> answers = ProfileQuestions.validate(req.answers());
        List<String> industries = cleanIndustries(req.industries());
        String location = req.location() == null || req.location().isBlank() ? null : req.location().trim();
        ProfileDraft draft = ai.buildProfile(answers, industries, location);

        Profile profile = profiles.findByUserId(userId).orElseGet(() -> new Profile(userId));
        profile.setSummary(draft.summary());
        profile.setSkills(draft.skills());
        profile.setEducation(draft.education());
        profile.setProjects(draft.projects());
        profile.setInterests(draft.interests());
        profile.setExperience(draft.experience());
        profile.setIndustries(industries);
        profile.setPreferredWorkType(draft.preferredWorkType());
        profile.setExperienceLevel(draft.experienceLevel());
        profile.setLocation(location);
        return profiles.save(profile);
    }

    @Transactional
    public Profile update(String userId, UpdateProfileRequest req) {
        Profile p = get(userId);
        if (req.summary() != null) p.setSummary(req.summary());
        if (req.skills() != null) p.setSkills(req.skills());
        if (req.education() != null) p.setEducation(req.education());
        if (req.projects() != null) p.setProjects(req.projects());
        if (req.interests() != null) p.setInterests(req.interests());
        if (req.experience() != null) p.setExperience(req.experience());
        if (req.industries() != null) p.setIndustries(cleanIndustries(req.industries()));
        if (req.preferredWorkType() != null) p.setPreferredWorkType(req.preferredWorkType());
        if (req.experienceLevel() != null) p.setExperienceLevel(req.experienceLevel());
        if (req.location() != null) p.setLocation(req.location());
        return profiles.save(p);
    }

    /** Trims, drops blanks and duplicates (case-insensitive); at most 3 remain. */
    static List<String> cleanIndustries(List<String> raw) {
        if (raw == null) {
            return List.of();
        }
        Map<String, String> unique = new LinkedHashMap<>();
        raw.stream().filter(i -> i != null && !i.isBlank()).map(String::trim)
                .forEach(i -> unique.putIfAbsent(i.toLowerCase(Locale.ROOT), i));
        if (unique.size() > 3) {
            throw ApiException.badRequest("industries can have at most 3 entries");
        }
        return List.copyOf(unique.values());
    }
}
