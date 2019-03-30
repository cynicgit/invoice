package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.Department;
import com.zhongyi.invoice.entity.Group;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.mapper.GroupMapper;
import com.zhongyi.invoice.service.GroupService;
import com.zhongyi.invoice.style.MyExcelExportStylerDefaultImpl;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/system/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @OperateLog("添加分组")
    @PostMapping
    public ZYResponse addGroup(@RequestBody Group group) throws Exception {
        groupService.addGroup(group);
        return ZYResponse.success();
    }

    @PutMapping
    @OperateLog("修改分组")
    public ZYResponse updateGroup(@RequestBody Group group) throws Exception {
        groupService.updateGroup(group);
        return ZYResponse.success();
    }

    @DeleteMapping("/{id}")
    @OperateLog("删除分组")
    public ZYResponse deleteGroup(@PathVariable Integer id) throws Exception {
        groupService.deleteGroupById(id);
        return ZYResponse.success();
    }

    @GetMapping
    public ZYResponse getGroup(Integer pageNum, Integer pageSize, String name) throws Exception {
        List<Group> pageInfo = groupService.getGroup( name);
        return ZYResponse.success(pageInfo);
    }
    @GetMapping("/member")
    public ZYResponse getGroupMember() throws Exception {
        List<Integer> pageInfo = groupService.getGroupMember();
        return ZYResponse.success(pageInfo);
    }

    @GetMapping("/{id}")
    public ZYResponse getGroup(@PathVariable Integer id) throws Exception {
        Group group = groupService.getGroupById(id);
        return ZYResponse.success(group);
    }

    @GetMapping("/all")
    public ZYResponse getGroupAll() throws Exception {
        List<Group> group = groupService.getGroupAll();
        return ZYResponse.success(group);
    }
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Group> groups = groupService.getGroup("");
        List<Group> list = new ArrayList<>();
        groups.forEach(group -> {
            if (!CollectionUtils.isEmpty(group.getUsers())) {
                group.getUsers().forEach(user -> {
                    Group g = new Group();
                    g.setName(group.getName());
                    g.setGroupMember(user.getName());
                    list.add(g);
                });
            }
        });

        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        exportParams.setStyle(MyExcelExportStylerDefaultImpl.class);
        EasyPoiUtils.defaultExport(list, Group.class,  "小组.xlsx", response, exportParams);
    }
}
