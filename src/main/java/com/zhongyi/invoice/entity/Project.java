package com.zhongyi.invoice.entity;

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

    private String projectName;

    private Date gmtCreate;
    private Date gmtModified;

    private String depName;

    private Integer depPid;
}
