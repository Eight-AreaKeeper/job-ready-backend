package com.jobready.mapper;

import com.jobready.pojo.entity.Mentor;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MentorMapper {

    void insert(Mentor mentor);

    /** Updates the signup fields (not points). */
    void update(Mentor mentor);

    Mentor getById(String id);

    Mentor getByUserId(String userId);

    List<Mentor> list();

    @Update("update mentors set points = points + #{amount} where id = #{id}")
    void addPoints(@Param("id") String id, @Param("amount") int amount);
}
