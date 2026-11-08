package com.jobready.service.impl;

import com.jobready.ai.AiClient;
import com.jobready.ai.AiModels.ProfileDraft;
import com.jobready.common.context.BaseContext;
import com.jobready.common.exception.BaseException;
import com.jobready.common.utils.IdUtil;
import com.jobready.mapper.ProfileMapper;
import com.jobready.pojo.dto.ProfileSubmitDTO;
import com.jobready.pojo.dto.ProfileUpdateDTO;
import com.jobready.pojo.entity.Profile;
import com.jobready.service.ProfileService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMapper profileMapper;
    private final AiClient aiClient;

    // Not @Transactional: the AI call can take a while and must not hold a DB connection.
    @Override
    public Profile submit(ProfileSubmitDTO dto) {
        String userId = BaseContext.getCurrentId();
        List<String> industries = dto.getIndustries() == null ? List.of() : dto.getIndustries();
        // Answers go straight to the AI and are not stored (no profile_answers table).
        ProfileDraft draft = aiClient.buildProfile(dto.getAnswers(), industries, dto.getLocation());

        Profile existing = profileMapper.getByUserId(userId);
        Profile profile = Profile.builder()
                .id(existing == null ? IdUtil.newId("p") : existing.getId())
                .userId(userId)
                .summary(draft.summary())
                .skills(draft.skills())
                .education(draft.education())
                .projects(draft.projects())
                .interests(draft.interests())
                .experience(draft.experience())
                .industries(industries)
                .preferredWorkType(draft.preferredWorkType())
                .experienceLevel(draft.experienceLevel())
                .location(dto.getLocation())
                .updatedAt(Instant.now())
                .build();
        if (existing == null) {
            profileMapper.insert(profile);
        } else {
            profileMapper.update(profile);
        }
        return profile;
    }

    @Override
    public Profile getCurrent() {
        Profile profile = profileMapper.getByUserId(BaseContext.getCurrentId());
        if (profile == null) {
            throw BaseException.notFound("profile");
        }
        return profile;
    }

    @Override
    public Profile findByUserId(String userId) {
        return profileMapper.getByUserId(userId);
    }

    @Override
    @Transactional
    public Profile update(ProfileUpdateDTO dto) {
        Profile p = getCurrent();
        if (dto.getSummary() != null) p.setSummary(dto.getSummary());
        if (dto.getSkills() != null) p.setSkills(dto.getSkills());
        if (dto.getEducation() != null) p.setEducation(dto.getEducation());
        if (dto.getProjects() != null) p.setProjects(dto.getProjects());
        if (dto.getInterests() != null) p.setInterests(dto.getInterests());
        if (dto.getExperience() != null) p.setExperience(dto.getExperience());
        if (dto.getIndustries() != null) p.setIndustries(dto.getIndustries());
        if (dto.getPreferredWorkType() != null) p.setPreferredWorkType(dto.getPreferredWorkType());
        if (dto.getExperienceLevel() != null) p.setExperienceLevel(dto.getExperienceLevel());
        if (dto.getLocation() != null) p.setLocation(dto.getLocation());
        p.setUpdatedAt(Instant.now());
        profileMapper.update(p);
        return p;
    }
}
