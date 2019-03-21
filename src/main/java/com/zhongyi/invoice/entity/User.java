package com.zhongyi.invoice.entity;

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
    private String name;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotNull(message = "请选择用户类型")
    private Integer type;

    private Integer depId;

    private Date gmtCreate;

    private Date gmtModified;

    private String depName;
    private Integer depPid;

}
