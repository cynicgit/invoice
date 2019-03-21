package com.zhongyi.invoice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Serializable {

    private Integer id;

    @NotBlank(message = "分组名不能为空")
    private String name;

    private Date gmtCreate;

    private Date gmtModified;

    private List<User> users;
}
