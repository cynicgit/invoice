package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
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
    @Excel(name = "子部门", orderNum = "1")
    private String name;

    private Integer pid;

    private Date gmtCreate;

    private Date gmtModified;
    @Excel(name = "父部门", orderNum = "0", mergeVertical = true)
    private String parentName;

    private List<Department> childrenDep = new ArrayList<>();

}
