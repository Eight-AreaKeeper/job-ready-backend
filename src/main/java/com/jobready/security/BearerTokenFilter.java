package com.jobready.security;

import com.jobready.users.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Reads "Authorization: Bearer &lt;token&gt;" and puts the user id in the security context.
 * Controllers get it with {@code @AuthenticationPrincipal String userId}.
 */
@Component
public class BearerTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository users;
    private final AppProperties props;

    public BearerTokenFilter(JwtService jwtService, UserRepository users, AppProperties props) {
        this.jwtService = jwtService;
        this.users = users;
        this.props = props;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Optional<String> userId = Optional.empty();
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            userId = jwtService.verify(header.substring(7).trim()).filter(users::existsById);
        } else if (props.auth().isDemo()) {
            userId = users.findByEmail(props.auth().demoUserEmail()).map(u -> u.getId());
        }
        userId.ifPresent(id -> SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(id, null, List.of())));
        chain.doFilter(request, response);
    }
}
