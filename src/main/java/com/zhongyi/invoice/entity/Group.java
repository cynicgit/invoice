package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zhongyi.invoice.controller.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Serializable {

    private Integer id;

    @NotBlank(message = "分组名不能为空")
    @Excel(name = "小组名", mergeVertical = true)
    private String name;

    private Date gmtCreate;

    private Date gmtModified;

    private List<User> users = new ArrayList<>();

    @Excel(name = "成员", orderNum = "1")
    private String groupMember;
}
