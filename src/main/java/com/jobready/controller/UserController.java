package com.jobready.controller;

import com.jobready.common.context.BaseContext;
import com.jobready.pojo.vo.UserVO;
import com.jobready.service.UserService;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(UserVO.from(userService.getById(BaseContext.getCurrentId())));
    }
}
