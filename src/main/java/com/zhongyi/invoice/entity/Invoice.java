package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
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
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开票日期", orderNum = "2", format = "yyyy.MM.dd", databaseFormat= "yyyy.MM.dd",exportFormat="yyyy.MM.dd" )
    private Date invoiceDate;
    @Excel(name = "信用类别", orderNum = "3")
    private String creditType;
    @Excel(name = "信用期限", orderNum = "4")
    private String creditLimit;

    @Excel(name = "发票类型", orderNum = "5")
    private String invoiceType;


    @Excel(name = "发票号", orderNum = "6")
    private String invoiceNumber;
    @Excel(name = "开票单位", orderNum = "7")
    private String invoiceOffice;
    @Excel(name = "所属部门", orderNum = "8")
    private String departmentName;

    private Integer depId;
    @Excel(name = "项目", orderNum = "9")
    private String invoiceProject;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "合同金额", orderNum = "10")
    private Double contractAmount;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "发票金额", orderNum = "11")
    private Double invoiceAmount;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "不含税金额", orderNum = "12")
    private Double noTaxAmount;
    @Excel(name = "项目负责人", orderNum = "13")
    private String contractUser;

    private Integer userId;
    @Excel(name = "签收人", orderNum = "14")
    private String invoiceSignatory;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Excel(name = "签收日期", orderNum = "15" , format = "yyyy.MM.dd")
    private Date invoiceSignatoryDate;
    @Excel(name = "报告号", orderNum = "16")
    private String reportNumber;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报告日期", orderNum = "17", format = "yyyy.MM.dd")
    private Date reportDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Excel(name = "回款日期", orderNum = "18",  format = "yyyy.MM.dd")
    private Date receivedDate;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "回款金额", orderNum = "19")
    private Double receivedAmount = 0.0;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "未到账", orderNum = "20")
    private Double noReceivedAmount = 0.0;

    @Excel(name = "备注", orderNum = "21")
    private String descprition;

    private Date gmtCreate;

    private Date gmtModified;


    private String errorMsg;

}
