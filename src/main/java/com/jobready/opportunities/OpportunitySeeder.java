package com.jobready.opportunities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Loads seed/opportunities.json into an empty opportunities table on startup.
 * TODO(team): replace the sample rows with the 20-30 real opportunities we pick.
 */
@Component
public class OpportunitySeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OpportunitySeeder.class);

    private final OpportunityRepository opportunities;
    private final ObjectMapper mapper;

    public OpportunitySeeder(OpportunityRepository opportunities, ObjectMapper mapper) {
        this.opportunities = opportunities;
        this.mapper = mapper;
    }

    record SeedRow(String id, String title, String organization, String type, String industry, String location,
            String scope, String description, List<String> requirements, LocalDate deadline, String url) {
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (opportunities.count() > 0) {
            return;
        }
        try (InputStream in = new ClassPathResource("seed/opportunities.json").getInputStream()) {
            List<SeedRow> rows = mapper.readValue(in, new TypeReference<>() {});
            opportunities.saveAll(rows.stream().map(r -> new Opportunity(r.id(), r.title(), r.organization(),
                    r.type(), r.industry(), r.location(), r.scope(), r.description(), r.requirements(),
                    r.deadline(), r.url())).toList());
            log.info("Seeded {} opportunities", rows.size());
        }
    }
}
