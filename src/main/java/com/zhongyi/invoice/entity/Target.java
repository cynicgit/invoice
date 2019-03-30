package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Target {

    private Integer id;

    @Excel(name = "小组/个人")
    private String name;

    @Excel(name = "目标", numFormat = "0.00", orderNum = "1")
    private Double target;

    private Integer type;
    @Excel(name = "年份",orderNum = "2")
    private String year;

    private Integer userId;

    private Integer groupId;

}
