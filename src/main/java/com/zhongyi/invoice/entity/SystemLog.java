package com.zhongyi.invoice.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/15.
 * @Description:
 */
@Getter
@Setter
public class SystemLog {
    private Integer id;
    private Date gmtCreate;
    private Date gmtModified;
    private String username;
    private String exception;
    private String method;
    private String params;
    private String requestUrl;
    private String description;
}
