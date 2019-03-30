package com.zhongyi.invoice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Target;
import com.zhongyi.invoice.mapper.TargetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TargetService {

    @Autowired
    private TargetMapper targetMapper;

    public void addTarget(Target target) throws Exception {
        Target t = targetMapper.getTargetByNameAndYear(target.getName(), target.getYear());
        if (t != null) {
            throw new Exception("已存在");
        }
        targetMapper.insertSelective(target);
    }

    public void updateTarget(Target target) {
        targetMapper.updateTarget(target);
    }

    public PageInfo<Target> getTarget(Integer pageNum, Integer pageSize, Target target) {
        PageHelper.startPage(pageNum, pageSize);
        List<Target> targets = targetMapper.getTarget(target);
        return new PageInfo<>(targets);
    }

    public Target getTargetById(Integer id) {
        Target target = targetMapper.getTargetById(id);
        return target;
    }

    public void deleteTarget(Integer id) {
        targetMapper.deleteTarget(id);
    }


    public List<Target> getAllUserTarget(String year) {
        return targetMapper.getAllUserTarget(year);
    }

    public List<Target> getAllGroupTarget(String year) {
        return targetMapper.getAllGroupTarget(year);
    }

}
