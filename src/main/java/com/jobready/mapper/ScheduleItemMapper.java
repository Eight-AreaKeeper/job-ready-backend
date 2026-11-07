package com.jobready.mapper;

import com.jobready.pojo.entity.ScheduleItem;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ScheduleItemMapper {

    @Insert("insert into schedule_items (id, user_id, opportunity_id, status, created_at) "
            + "values (#{id}, #{userId}, #{opportunityId}, #{status}, #{createdAt})")
    void insert(ScheduleItem item);

    @Select("select * from schedule_items where id = #{id}")
    ScheduleItem getById(String id);

    @Select("select * from schedule_items where user_id = #{userId}")
    List<ScheduleItem> listByUserId(String userId);

    @Select("select count(*) from schedule_items where user_id = #{userId} and opportunity_id = #{opportunityId}")
    int countByUserAndOpportunity(@Param("userId") String userId, @Param("opportunityId") String opportunityId);

    @Update("update schedule_items set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") String id, @Param("status") String status);

    @Delete("delete from schedule_items where id = #{id}")
    void deleteById(String id);
}
