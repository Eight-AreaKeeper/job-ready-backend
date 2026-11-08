package com.jobready.interceptor;

import com.jobready.common.context.BaseContext;
import com.jobready.common.exception.BaseException;
import com.jobready.common.properties.AppProperties;
import com.jobready.common.utils.JwtUtil;
import com.jobready.mapper.UserMapper;
import com.jobready.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Checks "Authorization: Bearer &lt;token&gt;" on every /api/v1/** request except /api/v1/auth/**,
 * and puts the user id in BaseContext. In demo auth mode a request without a token acts as the demo user.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final AppProperties props;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true; // static resources, CORS preflight
        }
        Optional<String> userId = Optional.empty();
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            userId = jwtUtil.parseUserId(header.substring(7).trim())
                    .filter(id -> userMapper.getById(id) != null);
        }
        if (userId.isEmpty() && props.auth().isDemo()) {
            // Demo mode: a missing or invalid token falls back to the demo user.
            userId = Optional.ofNullable(userMapper.getByEmail(props.auth().demoUserEmail())).map(User::getId);
        }
        String id = userId.orElseThrow(() -> BaseException.unauthorized("missing or invalid token"));
        BaseContext.setCurrentId(id);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        BaseContext.removeCurrentId();
    }
}
