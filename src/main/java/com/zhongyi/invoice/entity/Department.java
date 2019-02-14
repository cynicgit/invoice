package com.zhongyi.invoice.entity;

import com.alibaba.druid.filter.AutoLoad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    private Integer id;

    private String name;

    private Integer pid;

    private Date gmtCreate;

    private Date gmtModified;

    private String parentName;

    private List<Department> childrenDep = new ArrayList<>();

}
