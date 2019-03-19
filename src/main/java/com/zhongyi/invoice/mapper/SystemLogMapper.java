package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.SystemLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/19.
 * @Description:
 */
public interface SystemLogMapper {

    List<SystemLog> findAll(@Param("condition")String condition);

    void addLog(SystemLog systemLog);
}
