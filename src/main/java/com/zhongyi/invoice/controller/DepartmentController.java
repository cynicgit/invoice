package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Department;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dep")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ZYResponse addDep(Department department) throws Exception {
        departmentService.addDep(department);
        return ZYResponse.success();
    }

    @PutMapping
    public ZYResponse updateDep(Department department) {
        departmentService.updateDep(department);
        return ZYResponse.success();
    }

    @GetMapping("/parent")
    public ZYResponse getParentDep() {
        List<Department> list = departmentService.getParentDep();
        return ZYResponse.success(list);
    }

    @GetMapping("/{id}")
    public ZYResponse getDepById(@PathVariable Integer id) {
        Department department = departmentService.getDepById(id);
        return ZYResponse.success(department);
    }

    @DeleteMapping("/{id}")
    public ZYResponse deleteDep(@PathVariable Integer id) {
        departmentService.deleteDep(id);
        return ZYResponse.success();
    }

    @GetMapping("/tree")
    public ZYResponse tree() {
        List<Department> list = departmentService.getTreeDep();
        return ZYResponse.success(list);
    }

    @GetMapping
    public ZYResponse getDep(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, String name) {
        List<Department> pageInfo = departmentService.getDep(name);
        return ZYResponse.success(pageInfo);
    }

}
