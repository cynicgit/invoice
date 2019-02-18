package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Target;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.TargetService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/target")
public class TargetController {


    @Autowired
    private TargetService targetService;

    @PostMapping
    public ZYResponse addTarget(Target target) throws Exception {
        targetService.addTarget(target);
        return ZYResponse.success();
    }

    @PutMapping
    public ZYResponse updateTarget(Target target) throws Exception {
        targetService.updateTarget(target);
        return ZYResponse.success();
    }

    @GetMapping
    public ZYResponse getTarget(Integer pageNum, Integer pageSize, Target target) throws Exception {
        PageInfo<Target> pageInfo = targetService.getTarget(pageNum, pageSize, target);
        return ZYResponse.success(pageInfo);
    }

    @GetMapping("/{id}")
    public ZYResponse getTarget(@PathVariable Integer id) throws Exception {
        Target pageInfo = targetService.getTargetById(id);
        return ZYResponse.success(pageInfo);
    }

    @DeleteMapping("/{id}")
    public ZYResponse deleteTarget(@PathVariable Integer id) throws Exception {
        targetService.deleteTarget(id);
        return ZYResponse.success();
    }

}
