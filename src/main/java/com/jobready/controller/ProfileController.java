package com.jobready.controller;

import com.jobready.common.constant.ProfileQuestionConstant;
import com.jobready.common.result.ListResult;
import com.jobready.pojo.dto.ProfileSubmitDTO;
import com.jobready.pojo.dto.ProfileUpdateDTO;
import com.jobready.pojo.entity.Profile;
import com.jobready.pojo.vo.ProfileQuestionVO;
import com.jobready.service.ProfileService;
import jakarta.validation.Valid;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@Slf4j
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/questions")
    public Result<ListResult<ProfileQuestionVO>> questions() {
        return Result.success(ListResult.of(ProfileQuestionConstant.QUESTIONS));
    }

    /** AI builds the career profile from the answers (replaces any existing one). */
    @PostMapping
    public Result<Profile> submit(@Valid @RequestBody ProfileSubmitDTO dto) {
        log.info("Submit profile: {} answers", dto.getAnswers().size());
        return Result.success(profileService.submit(dto));
    }

    @GetMapping
    public Result<Profile> get() {
        return Result.success(profileService.getCurrent());
    }

    /** Send only the fields that change. */
    @PatchMapping
    public Result<Profile> update(@Valid @RequestBody ProfileUpdateDTO dto) {
        log.info("Update profile: {}", dto);
        return Result.success(profileService.update(dto));
    }
}
