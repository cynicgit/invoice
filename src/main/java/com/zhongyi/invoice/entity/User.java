package com.zhongyi.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;

    private String name;

    private String password;

    private Integer type;

    private Integer depId;

    private Date gmtCreate;

    private Date gmtModified;

    private String depName;
    private Integer depPid;

}
