package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.Project;
import com.zhongyi.invoice.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/29.
 * @Description:
 */
public interface ProjectMapper {

   // Project getUserByName(@Param("name") String name);

    int insertSelective(Project project);

    int updateProject(Project project);

    int deleteProjectById(Integer id);

    List<Project> getProject(@Param("projectName") String projectName);

    Project getProjectById(@Param("id") Integer id);



    List<Project> getProjectByDepId(@Param("depId") Integer depId);
}
