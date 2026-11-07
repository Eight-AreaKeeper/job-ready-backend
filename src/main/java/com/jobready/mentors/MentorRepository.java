package com.jobready.mentors;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, String> {

    Optional<Mentor> findByUserId(String userId);
}
