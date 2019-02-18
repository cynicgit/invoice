package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ZYResponse addUser(User user) throws Exception {
        userService.addUser(user);
        return ZYResponse.success();
    }

    @PutMapping
    public ZYResponse putUser(User user) throws Exception {
        userService.updateUser(user);
        return ZYResponse.success();
    }

    @DeleteMapping("/{id}")
    public ZYResponse deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ZYResponse.success();
    }

    @GetMapping
    public ZYResponse getUser(Integer pageNum, Integer pageSize, User user) {
        PageInfo<User> pageInfo = userService.getUser(pageNum, pageSize, user);
        return ZYResponse.success(pageInfo);
    }

    @GetMapping("/all")
    public ZYResponse all() {
        return ZYResponse.success(userService.getAllUser());
    }

    @GetMapping("/{id}")
    public ZYResponse getUserDeatil(@PathVariable Integer id) {
        User u = userService.getUserById(id);
        return ZYResponse.success(u);
    }

}
