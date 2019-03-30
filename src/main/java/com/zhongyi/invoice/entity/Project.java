package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/29.
 * @Description:
 */
@Setter
@Getter
public class Project {

    private Integer id;
    private Integer depId;
    @Excel(name = "项目名", orderNum = "1")
    private String projectName;

    private Date gmtCreate;
    private Date gmtModified;
    @Excel(name = "部门",mergeVertical = true)
    private String depName;

    private Integer depPid;
}
