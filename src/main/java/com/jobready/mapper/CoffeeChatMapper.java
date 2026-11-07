package com.jobready.mapper;

import com.jobready.pojo.entity.CoffeeChat;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CoffeeChatMapper {

    void insert(CoffeeChat chat);

    CoffeeChat getById(String id);

    /** Chats where the user is the student, or the mentor (mentorId may be null). */
    List<CoffeeChat> listByStudentOrMentor(@Param("studentId") String studentId,
            @Param("mentorId") String mentorId);

    /** Updates status, and scheduled_at when not null. */
    void update(CoffeeChat chat);
}
