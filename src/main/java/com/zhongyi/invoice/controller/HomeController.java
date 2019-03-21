package com.zhongyi.invoice.controller;

import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.UserService;
import com.zhongyi.invoice.utils.Md5Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@RestController
public class HomeController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ZYResponse login(User user, HttpSession session) {
       User u =  userService.getUserByName(user.getName());
       if (u == null) {
           return ZYResponse.error("用户名或密码错误");
       }
       if (!Objects.equals(user.getPassword(), u.getPassword())) {
           return ZYResponse.error("用户名或密码错误");
       }
       session.setAttribute("user", u);
       return ZYResponse.success(u);
    }

    @GetMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
       session.removeAttribute("user");
       response.sendRedirect("/login.html");
    }

}
