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

    /** The user's base resume (no opportunity, title "Base resume"); the newest if there are several. */
    Resume getBaseByUserId(String userId);

    /** Replaces the sections and created_at of an existing resume. */
    void updateSections(Resume resume);
}
