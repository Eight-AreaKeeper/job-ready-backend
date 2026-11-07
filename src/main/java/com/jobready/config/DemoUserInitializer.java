package com.jobready.config;

import com.jobready.security.AppProperties;
import com.jobready.users.User;
import com.jobready.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates a demo student account on startup so the front end can log in right away
 * (and so app.auth.mode=demo has a user to act as). Password: demo1234.
 */
@Component
public class DemoUserInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoUserInitializer.class);

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties props;

    public DemoUserInitializer(UserRepository users, PasswordEncoder passwordEncoder, AppProperties props) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.props = props;
    }

    @Override
    public void run(ApplicationArguments args) {
        String email = props.auth().demoUserEmail();
        if (email == null || email.isBlank() || users.existsByEmail(email)) {
            return;
        }
        users.save(new User("Demo Student", email, passwordEncoder.encode("demo1234"), User.ROLE_STUDENT));
        log.info("Created demo user {} (password demo1234), auth mode = {}", email, props.auth().mode());
    }
}
