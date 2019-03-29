package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Department;
import com.zhongyi.invoice.entity.Project;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.mapper.DepartmentMapper;
import com.zhongyi.invoice.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/29.
 * @Description:
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    public void addProject(Project project) {
        projectMapper.insertSelective(project);
    }

    public Project getProject(Integer id) {

        return projectMapper.getProjectById(id);
    }

    public void updateProject(Project project) {
        projectMapper.updateProject(project);
    }

    public void deleteProject(Integer id) {

        projectMapper.deleteProjectById(id);
    }

    public List<Project> getProjectByDepId(Integer depId) {

        //通过子id找到父Id

        Integer parentId = departmentMapper.getParentIdByChildId(depId);

        if (parentId == 0){
            parentId = depId;
        }

        return projectMapper.getProjectByDepId(parentId);

    }

    public PageInfo<Project> getProjectList(Integer pageNum, Integer pageSize, String projectName) {
        PageHelper.startPage(pageNum, pageSize);
        List<Project> list = projectMapper.getProject(projectName);

        list.stream().filter(u -> u.getDepPid() != null && u.getDepPid() != 0).forEach(u -> {
            Department depById = departmentMapper.getDepById(u.getDepPid());
            u.setDepName(depById.getName() + "-" + u.getDepName());
        });

        return new PageInfo<>(list);
    }
}
