package com.jobready.service;

import com.jobready.pojo.dto.ProfileSubmitDTO;
import com.jobready.pojo.dto.ProfileUpdateDTO;
import com.jobready.pojo.entity.Profile;

public interface ProfileService {

    /** AI builds the profile from the answers; replaces any existing profile. */
    Profile submit(ProfileSubmitDTO dto);

    /** 404 if the user has not built one yet. */
    Profile getCurrent();

    /** null if the user has no profile. */
    Profile findByUserId(String userId);

    Profile update(ProfileUpdateDTO dto);
}
