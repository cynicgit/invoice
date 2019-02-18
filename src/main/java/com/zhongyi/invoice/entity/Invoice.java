package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    private Integer id;
    @Excel(name = "任务单号", orderNum = "0")
    private String taskId;
    @Excel(name = "合同号", orderNum = "1")
    private String contractNumber;
    @Excel(name = "开票日期", orderNum = "2", format = "yyyy年MM月dd日")
    private Date invoiceDate;
    @Excel(name = "信用类别", orderNum = "3")
    private String creditType;
    @Excel(name = "信用期限", orderNum = "4")
    private String creditLimit;
    @Excel(name = "发票号", orderNum = "5")
    private String invoiceNumber;
    @Excel(name = "开票单位", orderNum = "6")
    private String invoiceOffice;
    @Excel(name = "所属部门", orderNum = "7")
    private String departmentName;

    private Integer depId;
    @Excel(name = "项目", orderNum = "8")
    private String invoiceProject;
    @Excel(name = "合同金额", orderNum = "9")
    private Double contractAmount;
    @Excel(name = "发票金额", orderNum = "10")
    private Double invoiceAmount;
    @Excel(name = "不含税金额", orderNum = "11")
    private Double noTaxAmount;
    @Excel(name = "项目负责人", orderNum = "12")
    private String contractUser;

    private Integer userId;
    @Excel(name = "开票性质", orderNum = "13")
    private String invoiceNature;
    @Excel(name = "签收人", orderNum = "14")
    private String invoiceSignatory;
    @Excel(name = "报告号", orderNum = "15")
    private String reportNumber;
    @Excel(name = "报告日期", orderNum = "16", format = "yyyy.MM.dd")
    private Date reportDate;
    @Excel(name = "回款日期", orderNum = "17")
    private Date receivedDate;
    @Excel(name = "回款金额", orderNum = "18")
    private Double receivedAmount = 0.0;
    @Excel(name = "未到账", orderNum = "19")
    private Double noReceivedAmount = 0.0;

    @Excel(name = "发票类型", orderNum = "20")
    private String invoiceType;


    private String errorMsg;

}
