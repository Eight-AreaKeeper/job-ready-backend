package com.jobready.users;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    /** GET /users/me */
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal String userId) {
        return UserDto.from(userService.get(userId));
    }
}
