package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.*;
import com.zhongyi.invoice.expection.BusinessException;
import com.zhongyi.invoice.mapper.DepartmentMapper;
import com.zhongyi.invoice.mapper.InvoiceMapper;
import com.zhongyi.invoice.mapper.UserMapper;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import com.zhongyi.invoice.utils.Md5Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    public String importExcel(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        List<UserInputDTO> userInputDTOS = EasyPoiUtils.importExcel(file, 0, 1, UserInputDTO.class);
        AtomicInteger count = new AtomicInteger();
        userInputDTOS.forEach(u -> {
            try {
                User user = new User();
                if (StringUtils.isEmpty(u.getName())) {
                    throw new BusinessException(u.getDepName() + "  用户名不能为空");
                }
                if (StringUtils.isEmpty(u.getPassword())) {
                    throw new BusinessException(u.getName() + " 密码不能为空");
                }
                if (StringUtils.isEmpty(u.getTypeName())) {
                    throw new BusinessException(u.getName() + "用户类型不能为空");
                }
                if (StringUtils.isEmpty(u.getDepName())) {
                    throw new BusinessException(u.getName() + "部门不能为空");
                }
                User byName = userMapper.getUserByName(u.getName());
                if (byName != null) {
                    throw new BusinessException(u.getName() + "用户名已存在");
                }
                String[] split = u.getDepName().split("-");
                if (split.length != 2) {
                    throw new BusinessException(u.getName() + "部门格式错误");
                }

                Department depByName = departmentMapper.getDepByName(split[1]);
                if (depByName == null) {
                    throw new BusinessException(u.getDepName() + "部门不存在");
                }

                user.setDepId(depByName.getId());

                user.setName(u.getName());
                user.setPassword(Md5Encrypt.string2MD5(u.getPassword()));
                if ("财务".equals(u.getTypeName())) {
                    user.setType(0);
                } else if ("商务经理".equals(u.getTypeName())) {
                    user.setType(1);
                } else if ("业务人员".equals(u.getTypeName())) {
                    user.setType(2);
                }
                count.getAndIncrement();
                userMapper.insertSelective(user);
            } catch (Exception e) {
                if (e instanceof BusinessException) {
                    sb.append(e.getMessage()).append("<br>");
                } else {
                    sb.append(u.getName()).append(e.getMessage()).append("<br>");
                }
            }

        });
        sb.append("导入：").append(count.intValue());
        return sb.toString();
    }
}
