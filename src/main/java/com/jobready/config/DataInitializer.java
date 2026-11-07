package com.jobready.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobready.common.constant.StatusConstant;
import com.jobready.common.properties.AppProperties;
import com.jobready.common.utils.IdUtil;
import com.jobready.mapper.OpportunityMapper;
import com.jobready.mapper.UserMapper;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.entity.User;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * On startup: creates the demo student (password demo1234) if missing, and loads
 * seed/opportunities.json when the opportunities table is empty.
 * TODO(team): replace the sample rows with the 20-30 real opportunities we pick.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final OpportunityMapper opportunityMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AppProperties props;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDemoUser();
        seedOpportunities();
    }

    private void createDemoUser() {
        String email = props.auth().demoUserEmail();
        if (email == null || email.isBlank() || userMapper.getByEmail(email) != null) {
            return;
        }
        userMapper.insert(User.builder()
                .id(IdUtil.newId("u"))
                .name("Demo Student")
                .email(email)
                .passwordHash(passwordEncoder.encode("demo1234"))
                .role(StatusConstant.ROLE_STUDENT)
                .createdAt(Instant.now())
                .build());
        log.info("Created demo user {} (password demo1234), auth mode = {}", email, props.auth().mode());
    }

    private void seedOpportunities() throws Exception {
        if (opportunityMapper.count() > 0) {
            return;
        }
        try (InputStream in = new ClassPathResource("seed/opportunities.json").getInputStream()) {
            List<Opportunity> rows = objectMapper.readValue(in, new TypeReference<>() {});
            for (Opportunity o : rows) {
                o.setCreatedAt(Instant.now());
                opportunityMapper.insert(o);
            }
            log.info("Seeded {} opportunities", rows.size());
        }
    }
}
