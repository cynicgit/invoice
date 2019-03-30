package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Department;
import com.zhongyi.invoice.mapper.DepartmentMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    public void addDep(Department department) throws Exception {
        Department d = departmentMapper.getDepByName(department.getName());
        if (d != null) {
            throw new RuntimeException("名字已存在");
        }
        departmentMapper.insertSelective(department);
    }

    public void updateDep(Department department) {
        departmentMapper.updateDep(department);
    }

    public List<Department> getParentDep() {
        return departmentMapper.getParentDep();
    }

    public List<Department> getDep(String name) {
        List<Department> list = departmentMapper.getDep(name);
        List<Department> departments = new ArrayList<>();
        List<Department> pList = list.stream().filter(d -> d.getPid() == 0).sorted(Comparator.comparing(Department::getId)).collect(Collectors.toList());
        pList.forEach(p -> {
            p.setParentName(p.getName());
            departments.add(p);
            List<Department> collect = list.stream().filter(d -> Objects.equals(d.getPid(), p.getId())).sorted(Comparator.comparing(Department::getId)).collect(Collectors.toList());
            collect.forEach(d -> d.setParentName(p.getName()));
            departments.addAll(collect);
        });
        return departments;
    }

    public void deleteDep(Integer id) {
        departmentMapper.deleteDep(id);
    }

    public Department getDepById(Integer id) {
        return departmentMapper.getDepById(id);
    }

    public List<Department> getTreeDep() {
        List<Department> list = departmentMapper.getDep(null);
        List<Department> pList = list.stream().filter(d -> d.getPid() == 0).sorted(Comparator.comparing(Department::getId)).collect(Collectors.toList());
        pList.forEach(p -> {
            List<Department> collect = list.stream().filter(d -> Objects.equals(d.getPid(), p.getId())).sorted(Comparator.comparing(Department::getId)).collect(Collectors.toList());
            p.getChildrenDep().addAll(collect);
        });
        return pList;
    }

    public List<Department> allDep() {

        return  departmentMapper.getDep(null);
    }
}
