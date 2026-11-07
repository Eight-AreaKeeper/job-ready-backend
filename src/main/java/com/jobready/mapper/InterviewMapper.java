package com.jobready.mapper;

import com.jobready.pojo.entity.Interview;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InterviewMapper {

    void insert(Interview interview);

    Interview getById(String id);

    void updateAnswers(Interview interview);
}
