package com.zhongyi.invoice.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    private Integer id;
    @NotBlank(message = "任务单号不能为空！")
    @Excel(name = "任务单号", orderNum = "0")
    private String taskId;
    @NotBlank(message = "合同号不能为空！")
    @Excel(name = "合同号", orderNum = "1")
    private String contractNumber;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开票日期", orderNum = "2", format = "yyyy.MM.dd", databaseFormat= "yyyy.MM.dd",exportFormat="yyyy.MM.dd" )
    private Date invoiceDate;
    @NotBlank(message = "信用类别不能为空！")
    @Excel(name = "信用类别", orderNum = "3")
    private String creditType;
    @NotBlank(message = "信用期限不能为空！")
    @Excel(name = "信用期限", orderNum = "4")
    private String creditLimit;

    @NotBlank(message = "发票类型不能为空！")
    @Excel(name = "发票类型", orderNum = "5")
    private String invoiceType;

    @NotBlank(message = "发票号不能为空！")
    @Excel(name = "发票号", orderNum = "6")
    private String invoiceNumber;
    @NotBlank(message = "开票单位不能为空！")
    @Excel(name = "开票单位", orderNum = "7")
    private String invoiceOffice;
    @NotBlank(message = "所属部门不能为空！")
    @Excel(name = "所属部门", orderNum = "8")
    private String departmentName;

    private Integer depId;
    @Excel(name = "项目", orderNum = "9")
    @NotBlank(message = "项目不能为空！")
    private String invoiceProject;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "合同金额", orderNum = "10", numFormat = "0.00")
    private Double contractAmount;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "发票金额", orderNum = "11", numFormat = "0.00")
    private Double invoiceAmount;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "不含税金额", orderNum = "12", numFormat = "0.00")
    private Double noTaxAmount;
    @NotBlank(message = "项目负责人不能为空！")
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
    @Excel(name = "回款金额", orderNum = "19",numFormat = "0.00")
    private Double receivedAmount = 0.0;
    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "未到账", orderNum = "20",numFormat = "0.00")
    private Double noReceivedAmount = 0.0;

    @Pattern(regexp = "^\\d+(\\.\\d+)?$",message = "请输入正确数字")
    @Excel(name = "坏账", orderNum = "21", numFormat = "0.00")
    private Double badAmount = 0.0;

    private Integer taxRate;
    private Integer projectId;

    @Excel(name = "备注", orderNum = "22")
    private String descprition;

    private Date gmtCreate;

    private Date gmtModified;


    private String errorMsg;

}
