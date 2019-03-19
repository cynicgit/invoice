package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.BasePageOutputDTO;
import com.zhongyi.invoice.entity.InvoiceVO;
import com.zhongyi.invoice.entity.SystemLog;
import com.zhongyi.invoice.mapper.SystemLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/19.
 * @Description:
 */
@Service
public class SystemLogService {

    @Autowired
    private SystemLogMapper systemLogMapper;

    public BasePageOutputDTO systemLogList(Integer pageSize, Integer pageNum,String condition) {
        BasePageOutputDTO basePageOutputDTO = new BasePageOutputDTO();
        PageHelper.startPage(pageNum, pageSize);
        List<SystemLog> list = systemLogMapper.findAll(condition);
        PageInfo<SystemLog> pageInfo = new PageInfo<>(list);
        basePageOutputDTO.setPage(pageInfo.getPages());
        basePageOutputDTO.setList(pageInfo.getList());
        basePageOutputDTO.setHasNext(pageInfo.isIsLastPage());
        basePageOutputDTO.setTotal(pageInfo.getTotal());

        return basePageOutputDTO;

    }


    public void  addLog(SystemLog systemLog){
        systemLogMapper.addLog(systemLog);
    }

}
