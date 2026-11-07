package com.jobready.profile;

import com.jobready.ai.AiClient;
import com.jobready.ai.AiModels.ProfileDraft;
import com.jobready.common.ApiException;
import com.jobready.profile.ProfileDtos.SubmitProfileRequest;
import com.jobready.profile.ProfileDtos.UpdateProfileRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profiles;
    private final ProfileAnswerRepository answers;
    private final AiClient ai;

    public ProfileService(ProfileRepository profiles, ProfileAnswerRepository answers, AiClient ai) {
        this.profiles = profiles;
        this.answers = answers;
        this.ai = ai;
    }

    public Optional<Profile> find(String userId) {
        return profiles.findByUserId(userId);
    }

    public Profile get(String userId) {
        return find(userId).orElseThrow(() -> ApiException.notFound("profile"));
    }

    /** Saves the answers, asks the AI to build a profile, and replaces any existing profile. */
    @Transactional
    public Profile submit(String userId, SubmitProfileRequest req) {
        answers.deleteByUserId(userId);
        List<ProfileAnswer> saved = req.answers().stream()
                .map(a -> new ProfileAnswer(userId, a.questionId(), a.answer()))
                .toList();
        answers.saveAll(saved);

        List<String> industries = req.industries() == null ? List.of() : req.industries();
        ProfileDraft draft = ai.buildProfile(saved, industries, req.location());

        Profile profile = profiles.findByUserId(userId).orElseGet(() -> new Profile(userId));
        profile.setSummary(draft.summary());
        profile.setSkills(draft.skills());
        profile.setInterests(draft.interests());
        profile.setIndustries(industries);
        profile.setExperienceLevel(draft.experienceLevel());
        profile.setLocation(req.location());
        return profiles.save(profile);
    }

    @Transactional
    public Profile update(String userId, UpdateProfileRequest req) {
        Profile p = get(userId);
        if (req.summary() != null) p.setSummary(req.summary());
        if (req.skills() != null) p.setSkills(req.skills());
        if (req.interests() != null) p.setInterests(req.interests());
        if (req.industries() != null) p.setIndustries(req.industries());
        if (req.experienceLevel() != null) p.setExperienceLevel(req.experienceLevel());
        if (req.location() != null) p.setLocation(req.location());
        return profiles.save(p);
    }
}
