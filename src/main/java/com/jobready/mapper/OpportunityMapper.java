package com.jobready.mapper;

import com.jobready.pojo.dto.OpportunityPageQueryDTO;
import com.jobready.pojo.entity.Opportunity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OpportunityMapper {

    void insert(Opportunity opportunity);

    Opportunity getById(String id);

    /** Filtered page, ordered by deadline. */
    List<Opportunity> pageQuery(OpportunityPageQueryDTO query);

    long countQuery(OpportunityPageQueryDTO query);

    /** All opportunities, or only one scope when scope is not null. */
    List<Opportunity> listByScope(@Param("scope") String scope);

    List<Opportunity> listByIds(@Param("ids") List<String> ids);

    @Select("select count(*) from opportunities")
    long count();
}
