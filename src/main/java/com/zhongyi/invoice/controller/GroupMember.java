package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class GroupMember {

    @Excel(name = "成员")
    private String name;


}
