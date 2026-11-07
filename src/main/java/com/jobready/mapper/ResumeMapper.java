package com.jobready.mapper;

import com.jobready.pojo.entity.Resume;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResumeMapper {

    void insert(Resume resume);

    Resume getById(String id);

    /** Newest first. */
    List<Resume> listByUserId(String userId);
}
