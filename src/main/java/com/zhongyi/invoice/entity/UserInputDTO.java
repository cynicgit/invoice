package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author rongbin.huang
 * @create 2019-04-06 下午8:31
 **/
@Data
public class UserInputDTO {

    @Excel(name = "用户名称", orderNum = "0")
    private String name;


    @Excel(name = "部门", orderNum = "1")
    private String depName;

    @Excel(name = "用户类型", orderNum = "2")
    private String typeName;


    @Excel(name = "密码", orderNum = "2")
    private String password;

}
