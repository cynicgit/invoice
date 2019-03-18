package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Group;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Transactional
    public void addGroup(Group group) throws Exception {
        Group g = groupMapper.getGroupByName(group.getName());
        if (g != null) {
            throw new RuntimeException("已存在");
        }
        List<User> users = group.getUsers();
        // TODO 用户校验
        groupMapper.insertSelective(group);
        for (User u : users) {
            Integer count = groupMapper.getMemberByUserId(u.getId());
            if (count != null && count > 0 ) {
                throw new RuntimeException("用户已加入小组");
            }
            groupMapper.insertMember(group.getId(), u.getId());
        }


    }

    public void updateGroup(Group group) {
        groupMapper.updateGroup(group);
        int c = groupMapper.deleteMemberByGroupId(group.getId());
        for (User u : group.getUsers()) {
            Integer count = groupMapper.getMemberByUserId(u.getId());
            if (count != null && count > 0 ) {
                throw new RuntimeException("用户已加入小组");
            }
            groupMapper.insertMember(group.getId(), u.getId());
        }
    }

    public void deleteGroupById(Integer id) {
        groupMapper.deleteGroupById(id);
        groupMapper.deleteMemberByGroupId(id);
    }

    public List<Group> getGroup(String name) {
     //   PageHelper.startPage(pageNum, pageSize);
        List<Group> list = groupMapper.getGroup(name);
        return list;
    }

    public Group getGroupById(Integer id) {
        return groupMapper.getGroupById(id);
    }

    public List<Integer> getGroupMember() {
        return groupMapper.getGroupMember();
    }

    public List<Group> getGroupAll() {
        return groupMapper.getGroupAll();
    }
}
