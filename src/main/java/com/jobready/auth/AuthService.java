package com.jobready.auth;

import com.jobready.auth.AuthDtos.AuthResponse;
import com.jobready.auth.AuthDtos.LoginRequest;
import com.jobready.auth.AuthDtos.RegisterRequest;
import com.jobready.common.ApiException;
import com.jobready.security.JwtService;
import com.jobready.users.User;
import com.jobready.users.UserDto;
import com.jobready.users.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw ApiException.conflict("email is already registered");
        }
        User user = users.save(new User(req.name().trim(), email, passwordEncoder.encode(req.password()), req.role()));
        return new AuthResponse(UserDto.from(user), jwtService.issue(user.getId()));
    }

    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmail(req.email().trim().toLowerCase())
                .filter(u -> passwordEncoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> ApiException.unauthorized("wrong email or password"));
        return new AuthResponse(UserDto.from(user), jwtService.issue(user.getId()));
    }
}
