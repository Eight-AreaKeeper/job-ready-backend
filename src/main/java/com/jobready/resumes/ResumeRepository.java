package com.jobready.resumes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, String> {

    List<Resume> findByUserIdOrderByCreatedAtDesc(String userId);
}
