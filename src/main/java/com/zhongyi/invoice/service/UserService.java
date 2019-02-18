package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Department;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.mapper.DepartmentMapper;
import com.zhongyi.invoice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    public void addUser(User user) throws Exception {
        User u = userMapper.getUserByName(user.getName());
        if (u != null) {
            throw new Exception("用户名已存在");
        }
        userMapper.insertSelective(user);

    }

    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    public void deleteUser(Integer id) {
        userMapper.deleteUserById(id);
    }

    public PageInfo<User> getUser(Integer page, Integer size, User user) {
        PageHelper.startPage(page, size);
        List<User> list = userMapper.getUser(user);
        list.stream().filter(u -> u.getDepPid() != 0).forEach(u -> {
            Department depById = departmentMapper.getDepById(u.getDepPid());
            u.setDepName(depById.getName() + "-" + u.getDepName());
        });

        return new PageInfo<>(list);
    }

    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    public List<User> getAllUser() {
        return userMapper.getUser(new User());
    }
}
