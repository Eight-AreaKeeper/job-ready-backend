package com.jobready.mapper;

import com.jobready.pojo.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into users (id, name, email, password_hash, role, created_at) "
            + "values (#{id}, #{name}, #{email}, #{passwordHash}, #{role}, #{createdAt})")
    void insert(User user);

    @Select("select * from users where id = #{id}")
    User getById(String id);

    @Select("select * from users where email = #{email}")
    User getByEmail(String email);

    List<User> listByIds(@Param("ids") List<String> ids);
}
