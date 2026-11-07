package com.jobready.coffeechats;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeChatRepository extends JpaRepository<CoffeeChat, String> {

    List<CoffeeChat> findByStudentIdOrMentorId(String studentId, String mentorId);
}
