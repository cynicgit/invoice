package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.Department;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.DepartmentService;
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
@RequestMapping("/system/dep")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    @OperateLog("部门添加")
    public ZYResponse addDep(Department department) throws Exception {
        departmentService.addDep(department);
        return ZYResponse.success();
    }

    @PutMapping
    @OperateLog("部门更新")
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
    @OperateLog("部门删除")
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

    @GetMapping("/all")
    public ZYResponse allDep() {
        List<Department> allDep = departmentService.allDep();
        return ZYResponse.success(allDep);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Department> list = departmentService.getTreeDep();
        List<Department> allDep = new ArrayList<>();
        list.forEach(department -> {
            if (CollectionUtils.isEmpty(department.getChildrenDep())) {
                department.setParentName(department.getName());
                allDep.add(department);
            } else {
                department.getChildrenDep().forEach(c -> {
                    c.setParentName(department.getName());
                    allDep.add(c);
                });
            }
        });

        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        exportParams.setStyle(MyExcelExportStylerDefaultImpl.class);
        EasyPoiUtils.defaultExport(allDep, Department.class,  "部门.xlsx", response, exportParams);
    }

}
