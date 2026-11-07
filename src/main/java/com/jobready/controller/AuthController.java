package com.jobready.controller;

import com.jobready.pojo.dto.UserLoginDTO;
import com.jobready.pojo.dto.UserRegisterDTO;
import com.jobready.pojo.vo.UserLoginVO;
import com.jobready.service.UserService;
import jakarta.validation.Valid;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/** Register and log in. No token needed. */
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<UserLoginVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        log.info("Register: {}", dto.getEmail());
        return Result.success(userService.register(dto));
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        log.info("Login: {}", dto.getEmail());
        return Result.success(userService.login(dto));
    }
}
