package com.zhongyi.invoice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable{

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
