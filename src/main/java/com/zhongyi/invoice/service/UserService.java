package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Department;
import com.zhongyi.invoice.entity.InvoiceVO;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.expection.BusinessException;
import com.zhongyi.invoice.mapper.DepartmentMapper;
import com.zhongyi.invoice.mapper.InvoiceMapper;
import com.zhongyi.invoice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private InvoiceMapper invoiceMapper;

    public void addUser(User user) throws Exception {
        User u = userMapper.getUserByName(user.getName());
        if (u != null) {
            throw new BusinessException("用户名已存在");
        }
        userMapper.insertSelective(user);

    }

    public void updateUser(User user) {
        User oldUser = userMapper.getUserById(user.getId());
        if (!StringUtils.isEmpty(user.getName()) && !user.getName().equals(oldUser.getName())) {
            User u = userMapper.getUserByName(user.getName());
            if (u != null) {
                throw new BusinessException("用户名已存在");
            }
            List<InvoiceVO> invoiceByUserName = invoiceMapper.getInvoiceByUserName(oldUser.getName());
            if (!CollectionUtils.isEmpty(invoiceByUserName)) {
                throw new BusinessException("该用户有开票数据，请先更改开票数据");
            }
        }


        userMapper.updateUser(user);
    }

    public void deleteUser(Integer id) {
        User user = userMapper.getUserById(id);
        List<InvoiceVO> invoiceByUserName = invoiceMapper.getInvoiceByUserName(user.getName());
        if (!CollectionUtils.isEmpty(invoiceByUserName)) {
            throw new BusinessException("该用户有开票数据，请先更改开票数据");
        }

        userMapper.deleteUserById(id);
    }

    public PageInfo<User> getUser(Integer page, Integer size, User user) {
        PageHelper.startPage(page, size);
        List<User> list = userMapper.getUser(user);
        list.stream().filter(u -> u.getDepPid() !=null &&  u.getDepPid() != 0).forEach(u -> {
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

    public User getUserByName(String name) {
        return userMapper.getUserByName(name);
    }
}
