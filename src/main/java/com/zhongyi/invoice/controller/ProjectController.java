package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Project;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/29.
 * @Description:
 */
@RequestMapping("/project")
@RestController
public class ProjectController {

    @Autowired
    public ProjectService projectService;

    @PostMapping
    public ZYResponse addProject(Project project){
        projectService.addProject(project);
        return ZYResponse.success();
    }

    @GetMapping("/{id}")
    public ZYResponse getProject(@PathVariable Integer id){
        Project project = projectService.getProject(id);
        return ZYResponse.success(project);
    }

    @PutMapping()
    public ZYResponse updateProject(Project project){
        projectService.updateProject(project);
        return ZYResponse.success();
    }

    @DeleteMapping("/{id}")
    public ZYResponse deleteProject(@PathVariable Integer id){
        projectService.deleteProject(id);
        return ZYResponse.success();
    }

    @GetMapping("/dep")
    public ZYResponse getProjectByDepId(Integer depId){
        List<Project> projects = projectService.getProjectByDepId(depId);
        return ZYResponse.success(projects);
    }

    @GetMapping("/page")
    public ZYResponse getProjectList(Integer pageNum,Integer pageSize,String projectName){
        PageInfo<Project> pageInfo = projectService.getProjectList(pageNum,pageSize,projectName);
        return ZYResponse.success(pageInfo);
    }
}
