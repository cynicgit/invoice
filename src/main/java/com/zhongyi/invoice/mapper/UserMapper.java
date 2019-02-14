package com.zhongyi.invoice.mapper;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Department;
import com.zhongyi.invoice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserMapper {


    User getUserByName(@Param("name") String name);

    int insertSelective(User user);

    int updateUser(User user);

    int deleteUserById(Integer id);

    List<User> getUser(User user);

    User getUserById(@Param("id") Integer id);
}
