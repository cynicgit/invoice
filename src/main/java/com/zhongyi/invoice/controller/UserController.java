package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.UserService;
import com.zhongyi.invoice.style.MyExcelExportStylerDefaultImpl;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @OperateLog("用户添加")
    public ZYResponse addUser(@Valid User user) throws Exception {
        userService.addUser(user);
        return ZYResponse.success();
    }

    @PutMapping
    @OperateLog("用户更新")
    public ZYResponse putUser(User user) throws Exception {
        userService.updateUser(user);
        return ZYResponse.success();
    }

    @DeleteMapping("/{id}")
    @OperateLog("用户删除")
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

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<User> allUser = userService.getAllUser();
        allUser.forEach(u -> {
            if (u.getType() == 0) {
                u.setTypeName("财务");
            } else if (u.getType() == 1) {
                u.setTypeName("商务经理");
            } else if (u.getType() == 2) {
                u.setTypeName("业务人员");
            }
        });
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        exportParams.setStyle(MyExcelExportStylerDefaultImpl.class);
        EasyPoiUtils.defaultExport(allUser, User.class,  "用户.xlsx", response, exportParams);
    }

}
