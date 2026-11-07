package com.jobready.profile;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileAnswerRepository extends JpaRepository<ProfileAnswer, String> {

    List<ProfileAnswer> findByUserId(String userId);

    void deleteByUserId(String userId);
}
