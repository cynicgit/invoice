package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.Group;
import com.zhongyi.invoice.entity.Target;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component
public interface GroupMapper {


    Group getGroupByName(@Param("name") String name);

    int insertSelective(Group group);

    int updateGroup(Group group);

    int deleteGroupById(@Param("id") Integer id);

    List<Group> getGroup(@Param("name") String name);

    Group getGroupById(@Param("id") Integer id);

    int insertMember(@Param("groupId") Integer groupId, @Param("userId") Integer userId);

    Integer getMemberByUserId(@Param("id")Integer id);

    int deleteMemberByGroupId(@Param("groupId") Integer groupId);

    List<Integer> getGroupMember();

}
