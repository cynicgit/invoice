package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.Target;
import com.zhongyi.invoice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component
public interface TargetMapper {


    int insertSelective(Target target);

    Target getTargetByNameAndYear(@Param("userId") Integer userId, @Param("year") Date year);

    int updateTarget(Target target);

    List<Target> getTarget(Target target);

    Target getTargetById(@Param("id") Integer id);

    int deleteTarget(@Param("id") Integer id);
}
