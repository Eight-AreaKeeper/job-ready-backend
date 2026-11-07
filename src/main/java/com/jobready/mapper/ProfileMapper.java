package com.jobready.mapper;

import com.jobready.pojo.entity.Profile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfileMapper {

    Profile getByUserId(String userId);

    void insert(Profile profile);

    /** Updates every column except id and user_id. */
    void update(Profile profile);
}
