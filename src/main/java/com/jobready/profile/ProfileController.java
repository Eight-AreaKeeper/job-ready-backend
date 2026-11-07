package com.jobready.profile;

import com.jobready.common.ListResponse;
import com.jobready.profile.ProfileDtos.ProfileDto;
import com.jobready.profile.ProfileDtos.SubmitProfileRequest;
import com.jobready.profile.ProfileDtos.UpdateProfileRequest;
import com.jobready.profile.ProfileQuestions.Question;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /** GET /profile/questions */
    @GetMapping("/questions")
    public ListResponse<Question> questions() {
        return ListResponse.of(ProfileQuestions.ALL);
    }

    /** POST /profile -> 201, AI builds the career profile (replaces any existing one). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileDto submit(@AuthenticationPrincipal String userId, @Valid @RequestBody SubmitProfileRequest req) {
        return ProfileDto.from(profileService.submit(userId, req));
    }

    /** GET /profile -> 404 if the user has not built one yet. */
    @GetMapping
    public ProfileDto get(@AuthenticationPrincipal String userId) {
        return ProfileDto.from(profileService.get(userId));
    }

    /** PATCH /profile -> send only the fields that change. */
    @PatchMapping
    public ProfileDto update(@AuthenticationPrincipal String userId, @Valid @RequestBody UpdateProfileRequest req) {
        return ProfileDto.from(profileService.update(userId, req));
    }
}
