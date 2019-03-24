package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.Group;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
