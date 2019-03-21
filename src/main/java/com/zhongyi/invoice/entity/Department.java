package com.zhongyi.invoice.entity;

import com.alibaba.druid.filter.AutoLoad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    private Integer id;
    @NotBlank(message = "部门不能为空")
    private String name;

    private Integer pid;

    private Date gmtCreate;

    private Date gmtModified;

    private String parentName;

    private List<Department> childrenDep = new ArrayList<>();

}
