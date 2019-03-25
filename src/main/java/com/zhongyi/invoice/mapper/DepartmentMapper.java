package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DepartmentMapper {

    int insertSelective(Department department);

    int updateDep(Department department);

    List<Department> getParentDep();

    List<Department> getDep(@Param("name") String name);

    int deleteDep(@Param("id")Integer id);

    Department getDepById(@Param("id") Integer id);

    Department getDepByName(@Param("name") String name);

    String getParentName(@Param("departmentName") String departmentName);
}
