package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable{

    private Integer id;
    @NotBlank(message = "用户不能为空")
    @Excel(name = "用户名称", orderNum = "0")
    private String name;
    @NotBlank(message = "密码不能为空")
    private String password;


    @NotNull(message = "请选择用户类型")
    private Integer type;

    private Integer depId;

    private Date gmtCreate;

    private Date gmtModified;
    @Excel(name = "部门", orderNum = "1")
    private String depName;
    @Excel(name = "用户类型", orderNum = "2")
    private String typeName;
    private Integer depPid;

}
