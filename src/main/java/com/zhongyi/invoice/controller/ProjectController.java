package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Project;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.ProjectService;
import com.zhongyi.invoice.style.MyExcelExportStylerDefaultImpl;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Project> allProject = projectService.getAllProject();

        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        exportParams.setStyle(MyExcelExportStylerDefaultImpl.class);
        EasyPoiUtils.defaultExport(allProject, Project.class,  "项目.xlsx", response, exportParams);
    }

}
