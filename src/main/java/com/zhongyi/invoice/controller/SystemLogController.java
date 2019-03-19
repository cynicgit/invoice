package com.zhongyi.invoice.controller;

import com.zhongyi.invoice.entity.BasePageOutputDTO;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/19.
 * @Description:
 */
@RestController
@RequestMapping("/system/log")
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    @GetMapping("/page")
    public ZYResponse systemLogList(@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "1")Integer pageNum
            ,@RequestParam(required = false)String condition){
        BasePageOutputDTO basePageOutputDTO = systemLogService.systemLogList(pageSize, pageNum,condition);
        return ZYResponse.success(basePageOutputDTO);

    }
}
