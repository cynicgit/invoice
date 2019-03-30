package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.InvoiceVO;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.service.InvoiceService;
import com.zhongyi.invoice.utils.DateUtils;
import com.zhongyi.invoice.utils.DoubleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/17.
 * @Description:
 */
@RestController
@RequestMapping("/receipt")
@Slf4j
public class ReceiptController {

    @Autowired
    private InvoiceService invoiceService;

    @Value("${excelPath}")
    private String excelPath;



    @GetMapping("/detail/dep")
    @OperateLog("发票统计明细导出")
    public void receiptDetailByDepId(InvoiceVO invoiceVO,String condition, HttpServletResponse response,HttpServletRequest request) throws IOException {
        invoiceVO.setDepartmentName(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelReceiptDetail(invoiceVO);
        setCreateLimitPart(invoiceVOS);
        if (!StringUtils.isEmpty(invoiceVO.getDepartmentName())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(Invoice::getDepartmentName));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepartmentName()));
        }

        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        String path =  excelPath + "receiptDetail.xlsx";

        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计明细按部门统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    @GetMapping("/detail/contractUser")
    @OperateLog("发票统计明细导出")
    public void receiptDetailByContractUser(InvoiceVO invoiceVO, String condition,HttpServletResponse response, HttpServletRequest request) throws IOException {
        invoiceVO.setContractUser(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelReceiptDetail(invoiceVO);
        setCreateLimitPart(invoiceVOS);
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getContractUser()));
        }
        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        String path =  excelPath + "receiptDetail.xlsx";

        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计明细按项目负责人统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    @GetMapping("/detail/invoiceType")
    @OperateLog("发票统计明细导出")
    public void receiptDetailByInvoiceType(InvoiceVO invoiceVO,String condition, HttpServletResponse response,HttpServletRequest request) throws IOException {
        invoiceVO.setInvoiceType(condition);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelReceiptDetail(invoiceVO);
        setCreateLimitPart(invoiceVOS);
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceType())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceType));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getInvoiceType()));
        }

        if (user.getType() == 2){
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getContractUser()));
            invoiceVOS = map.get(user.getName());
        }

        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        String path =  excelPath + "receiptDetail.xlsx";

        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计明细按发票类型统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    @GetMapping("/detail/invoiceOffice")
    @OperateLog("发票统计明细导出")
    public void receiptDetailByInvoiceOffice(InvoiceVO invoiceVO,String condition, HttpServletResponse response,HttpServletRequest request) throws IOException {

        invoiceVO.setInvoiceOffice(condition);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelReceiptDetail(invoiceVO);
        setCreateLimitPart(invoiceVOS);
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceOffice));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getInvoiceOffice()));
        }

        if (user.getType() == 2){
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getContractUser()));
            invoiceVOS = map.get(user.getName());
        }
        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        String path =  excelPath + "receiptDetail.xlsx";
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计明细按开票单位统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    @GetMapping("/gather/dep")
    @OperateLog("发票汇总导出")
    public void receiptGatherByDepId(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setDepartmentName(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.receiptGatherStatistics(invoiceVO);

        String year = getYear(invoiceVO);
        List<InvoiceVO> thisYear = invoiceService.receiptGatherYearStatistics(year,invoiceVO);



        List<InvoiceVO> list = new ArrayList<>();
        String path =  excelPath + "receiptGatherDep.xlsx";
        //专票
        List<InvoiceVO> specialInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "专".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
        //普票
        List<InvoiceVO> commonInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "普".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        Double sumSpecialInvoiceAmount;
        Double sumSpecialNoTaxAmount;
        Double sumCommonInvoiceAmount;
        Double sumCommonNoTaxAmount;
        Double sumNoTaxAmount;
        Double sumInvoiceAmount;
        Double sumThisYearNoTaxAmount;
        Double sumThisYearInvoiceAmount;

        sumSpecialInvoiceAmount = DoubleUtil.getExactDouble(specialInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        //不含税金额
        sumSpecialNoTaxAmount = DoubleUtil.getExactDouble(specialInvoices.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());

        sumCommonInvoiceAmount = DoubleUtil.getExactDouble(commonInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        //不含税金额
        sumCommonNoTaxAmount = DoubleUtil.getExactDouble(commonInvoices.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());

        sumInvoiceAmount = DoubleUtil.getExactDouble(invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        sumNoTaxAmount = DoubleUtil.getExactDouble(invoiceVOS.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());

        sumThisYearInvoiceAmount = DoubleUtil.getExactDouble(thisYear.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        sumThisYearNoTaxAmount = DoubleUtil.getExactDouble(thisYear.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());

        InvoiceVO invoiceVO1 = new InvoiceVO();
        //按部门统计
        if (StringUtils.isEmpty(invoiceVO.getDepartmentName())) {
            Map<String, List<InvoiceVO>> depMap = invoiceVOS.stream().collect(Collectors.groupingBy(Invoice::getDepartmentName));
            Map<String, List<InvoiceVO>> thisYearDepMap = thisYear.stream().collect(Collectors.groupingBy(Invoice::getDepartmentName));
            getReceiptGatherStatistics(list, depMap, thisYearDepMap, "departmentName");
            //  getReceiptGatherThisYearStatistics(list,thisYearDepMap);
        } else {
            InvoiceVO invoiceVO2 = new InvoiceVO();
            invoiceVO2.setDepartmentName(invoiceVOS.get(0).getDepartmentName());
            invoiceVO2.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
            invoiceVO2.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
            invoiceVO2.setCommonInvoiceAmount(sumCommonInvoiceAmount);
            invoiceVO2.setCommonNoTaxAmount(sumCommonNoTaxAmount);
            invoiceVO2.setTotalNoTaxAmount(sumNoTaxAmount);
            invoiceVO2.setTotalInvoiceAmount(sumInvoiceAmount);
            invoiceVO2.setTotalThisYearInvoiceAmount(sumThisYearInvoiceAmount);
            invoiceVO2.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
            list.add(invoiceVO2);
        }
        invoiceVO1.setDepartmentName("合计");


        invoiceVO1.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
        invoiceVO1.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
        invoiceVO1.setCommonInvoiceAmount(sumCommonInvoiceAmount);
        invoiceVO1.setCommonNoTaxAmount(sumCommonNoTaxAmount);
        invoiceVO1.setTotalNoTaxAmount(sumNoTaxAmount);
        invoiceVO1.setTotalInvoiceAmount(sumInvoiceAmount);
        invoiceVO1.setTotalThisYearInvoiceAmount(sumThisYearInvoiceAmount);
        invoiceVO1.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
        list.add(invoiceVO1);
        map.put("list", list);
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计汇总按部门统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


    @GetMapping("/gather/contractUser")
    @OperateLog("发票汇总导出")
    public void receiptGatherByContractUser(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setContractUser(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.receiptGatherStatistics(invoiceVO);

        String year = getYear(invoiceVO);
         List<InvoiceVO> thisYear = invoiceService.receiptGatherYearStatistics(year,invoiceVO);
        List<InvoiceVO> list = new ArrayList<>();
        String path = excelPath + "receiptGatherUser.xlsx";
        Map<String, Object> mapParms = new HashMap<>();
        //专票
        List<InvoiceVO> specialInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "专".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
        //普票
        List<InvoiceVO> commonInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "普".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        Double sumSpecialInvoiceAmount;
        Double sumSpecialNoTaxAmount;
        Double sumCommonInvoiceAmount;
        Double sumCommonNoTaxAmount;
        Double sumThisYearNoTaxAmount;
        Double sumThisYearInvoiceAmount;
        Double sumNoTaxAmount;
        Double sumInvoiceAmount;
        sumSpecialInvoiceAmount = DoubleUtil.getExactDouble(specialInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        //不含税金额
        sumSpecialNoTaxAmount = DoubleUtil.getExactDouble(specialInvoices.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());

        sumCommonInvoiceAmount = DoubleUtil.getExactDouble(commonInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        //不含税金额
        sumCommonNoTaxAmount = DoubleUtil.getExactDouble(commonInvoices.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());


        sumNoTaxAmount = DoubleUtil.getExactDouble(invoiceVOS.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());
        sumInvoiceAmount = DoubleUtil.getExactDouble(invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        sumThisYearInvoiceAmount = DoubleUtil.getExactDouble(thisYear.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        sumThisYearNoTaxAmount =DoubleUtil.getExactDouble(thisYear.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());


        InvoiceVO invoiceVO1 = new InvoiceVO();
        //按开票人
        if (StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> contractUserMap = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            Map<String, List<InvoiceVO>> thisYearDepMap = thisYear.stream().collect(Collectors.groupingBy(Invoice::getContractUser));
            getReceiptGatherStatistics(list, contractUserMap, thisYearDepMap, "contractUser");

        } else {
            InvoiceVO invoiceVO2 = new InvoiceVO();
            invoiceVO2.setContractUser(invoiceVO.getContractUser());
            invoiceVO2.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
            invoiceVO2.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
            invoiceVO2.setCommonInvoiceAmount(sumCommonInvoiceAmount);
            invoiceVO2.setCommonNoTaxAmount(sumCommonNoTaxAmount);
            invoiceVO2.setTotalInvoiceAmount(sumInvoiceAmount);
            invoiceVO2.setTotalNoTaxAmount(sumNoTaxAmount);
            invoiceVO2.setTotalThisYearInvoiceAmount(sumThisYearInvoiceAmount);
            invoiceVO2.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
            list.add(invoiceVO2);
        }
        invoiceVO1.setContractUser("合计");
        invoiceVO1.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
        invoiceVO1.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
        invoiceVO1.setCommonInvoiceAmount(sumCommonInvoiceAmount);
        invoiceVO1.setCommonNoTaxAmount(sumCommonNoTaxAmount);
        invoiceVO1.setTotalInvoiceAmount(sumInvoiceAmount);
        invoiceVO1.setTotalNoTaxAmount(sumNoTaxAmount);
        invoiceVO1.setTotalThisYearInvoiceAmount(sumThisYearInvoiceAmount);
        invoiceVO1.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
        list.add(invoiceVO1);
        map.put("list", list);
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计汇总按项目负责人统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    private String getYear(InvoiceVO invoiceVO) {
        String date = null;
        if (StringUtils.isEmpty(invoiceVO.getStartDate()) && StringUtils.isEmpty(invoiceVO.getEndDate())){
            date = DateUtils.date2String(new Date()).split("-")[0];
        }else {
            date = invoiceVO.getStartDate().substring(0,4);
        }
        return  date;
    }


    @GetMapping("/gather/invoiceType")
    @OperateLog("发票汇总导出")
    public void receiptGatherByInvoiceType(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setInvoiceType(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.receiptGatherStatistics(invoiceVO);
        String year = getYear(invoiceVO);
        List<InvoiceVO> thisYear = invoiceService.receiptGatherYearStatistics(year,invoiceVO);
        List<InvoiceVO> list = new ArrayList<>();
       // String path =  "static/excel/receiptGatherType.xlsx";
        String path = excelPath + "receiptGatherType.xlsx";
        Map<String, Object> mapParms = new HashMap<>();

        List<InvoiceVO> thisYearCommonInvoices = thisYear.stream().filter(invoiceVO1 -> "普".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());

        List<InvoiceVO> thisYearSpecialInvoices = thisYear.stream().filter(invoiceVO1 -> "专".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());


        Double sumThisYearNoTaxAmount;
        Double sumThisYearInvoiceAmount;
        Double totalInvoiceAmount1;
        Double totalNoTaxAmount1;

        totalInvoiceAmount1 = DoubleUtil.getExactDouble(invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        totalNoTaxAmount1 = DoubleUtil.getExactDouble(invoiceVOS.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());


        sumThisYearInvoiceAmount = DoubleUtil.getExactDouble(thisYear.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        sumThisYearNoTaxAmount = DoubleUtil.getExactDouble(thisYear.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());

        Map<String, List<InvoiceVO>> invoiceTypMap = invoiceVOS.stream().collect(Collectors.groupingBy(Invoice::getInvoiceType));
        Map<String, List<InvoiceVO>> thisYearInvoiceTypMap = invoiceVOS.stream().collect(Collectors.groupingBy(Invoice::getInvoiceType));


        invoiceTypMap.forEach((key, list1) -> {
            InvoiceVO invoiceVO1 = new InvoiceVO();
            invoiceVO1.setInvoiceType(key);
            Double totalInvoiceAmount = getInvoiceAmount(list1);
            invoiceVO1.setCommonInvoiceAmount(totalInvoiceAmount);
            Double totalNoTaxAmount = getNoTaxAmount(list1);
            invoiceVO1.setCommonNoTaxAmount(totalNoTaxAmount);


            thisYearInvoiceTypMap.forEach((key2, list2) -> {
                if (key.equals(key2)) {
                    Double thisYearTotalInvoiceAmount = getInvoiceAmount(list2);
                    invoiceVO1.setTotalThisYearInvoiceAmount(thisYearTotalInvoiceAmount);
                    Double thisYearTotalNoTaxAmount = getNoTaxAmount(list2);
                    invoiceVO1.setTotalThisYearNoTaxAmount(thisYearTotalNoTaxAmount);
                }
            });

            list.add(invoiceVO1);
        });

        InvoiceVO invoiceVO1 = new InvoiceVO();
        invoiceVO1.setInvoiceType("合计");
        invoiceVO1.setCommonInvoiceAmount(totalInvoiceAmount1);
        invoiceVO1.setCommonNoTaxAmount(totalNoTaxAmount1);
        invoiceVO1.setTotalThisYearInvoiceAmount(sumThisYearInvoiceAmount);
        invoiceVO1.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
        list.add(invoiceVO1);
        mapParms.put("list", list);
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计汇总按发票性质统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());

//        String path = "static/excel/receiptGatherDep.xlsx";
//        Map<String, Object> mapParms = new HashMap<>();
//        //专票
//        List<InvoiceVO> specialInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "专".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
//        //普票
//        List<InvoiceVO> commonInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "普".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
//        Map<String, Object> map = new HashMap<>();
//        Double sumSpecialInvoiceAmount;
//        Double sumSpecialNoTaxAmount;
//        Double sumCommonInvoiceAmount;
//        Double sumCommonNoTaxAmount;
//        Double sumThisYearNoTaxAmount;
//        Double sumThisYearInvoiceAmount;
//
//        sumSpecialInvoiceAmount = specialInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
//        //不含税金额
//        sumSpecialNoTaxAmount = specialInvoices.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();
//
//        sumCommonInvoiceAmount = commonInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
//        //不含税金额
//        sumCommonNoTaxAmount = commonInvoices.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();
//
//        sumThisYearInvoiceAmount = thisYear.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
//        sumThisYearNoTaxAmount = thisYear.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();
//
//
//        InvoiceVO invoiceVO1 = new InvoiceVO();
//        if (!StringUtils.isEmpty(invoiceVO.getInvoiceType())) {
//            path = "专".equals(invoiceVO.getInvoiceType()) ?
//                    "static/excel/receiptGatherSpecial.xlsx" : "static/excel/receiptGatherCommon.xlsx";
//            sumCommonInvoiceAmount = "专".equals(invoiceVO.getInvoiceType()) ?
//                    getInvoiceAmount(specialInvoices) : getInvoiceAmount(commonInvoices);
//            sumCommonNoTaxAmount = "专".equals(invoiceVO.getInvoiceType()) ?
//                    getNoTaxAmount(specialInvoices) : getNoTaxAmount(commonInvoices);
//            InvoiceVO invoiceVO2 = new InvoiceVO();
//            invoiceVO2.setInvoiceType(invoiceVO.getInvoiceType());
//            invoiceVO2.setCommonInvoiceAmount(sumCommonInvoiceAmount);
//
//            sumCommonInvoiceAmount = "专".equals(invoiceVO.getInvoiceType()) ?
//                    getInvoiceAmount(specialInvoices) : getInvoiceAmount(commonInvoices);
//            sumCommonNoTaxAmount = "专".equals(invoiceVO.getInvoiceType()) ?
//                    getNoTaxAmount(specialInvoices) : getNoTaxAmount(commonInvoices)
//            invoiceVO2.setCommonNoTaxAmount(sumCommonNoTaxAmount);
//            invoiceVO2.setTotalThisYearInvoiceAmountt(sumThisYearInvoiceAmount);
//            invoiceVO2.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
//            list.add(invoiceVO2);
//            invoiceVO1.setInvoiceType("合计");
//        }
//        Resource resource = new ClassPathResource(path);
//        String filePath = ((ClassPathResource) resource).getPath();
//
//
//        invoiceVO1.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
//        invoiceVO1.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
//        invoiceVO1.setCommonInvoiceAmount(sumCommonInvoiceAmount);
//        invoiceVO1.setCommonNoTaxAmount(sumCommonNoTaxAmount);
//        invoiceVO1.setTotalThisYearInvoiceAmountt(sumThisYearInvoiceAmount);
//        invoiceVO1.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
//        list.add(invoiceVO1);
//        map.put("list", list);
//        TemplateExportParams params = new TemplateExportParams();
//        params.setTemplateUrl(path);
//        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("content-Type", "application/vnd.ms-excel");
//        response.setHeader("Content-Disposition",
//                "attachment;filename=" + URLEncoder.encode("发票统计汇总按发票性质统计.xlsx", "UTF-8"));
//        workbook.write(response.getOutputStream());


    }

    @GetMapping("/gather/invoiceOffice")
    @OperateLog("发票汇总导出")
    public void receiptGatherByInvoiceOffice(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setInvoiceOffice(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.receiptGatherStatistics(invoiceVO);
        String year = getYear(invoiceVO);
        List<InvoiceVO> thisYear = invoiceService.receiptGatherYearStatistics(year,invoiceVO);


        List<InvoiceVO> list = new ArrayList<>();
        String path = excelPath + "receiptGatherOffice.xlsx";

        //专票
        //  List<InvoiceVO> specialInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "专".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
        //普票
        //  List<InvoiceVO> commonInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "普".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        Double sumCommonInvoiceAmount;
        Double sumCommonNoTaxAmount;
        Double sumThisYearNoTaxAmount;
        Double sumThisYearInvoiceAmount;

        sumCommonInvoiceAmount = DoubleUtil.getExactDouble(invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        //不含税金额
        sumCommonNoTaxAmount = DoubleUtil.getExactDouble(invoiceVOS.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());

        sumThisYearInvoiceAmount = DoubleUtil.getExactDouble(thisYear.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum());
        sumThisYearNoTaxAmount = DoubleUtil.getExactDouble(thisYear.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum());

        InvoiceVO invoiceVO1 = new InvoiceVO();
        //按开票人
        if (StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
            Map<String, List<InvoiceVO>> contractUserMap = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceOffice));

            Map<String, List<InvoiceVO>> thisYearDepMap = thisYear.stream().collect(Collectors.groupingBy(Invoice::getInvoiceOffice));
            getReceiptGatherStatistics(list, contractUserMap, thisYearDepMap, "invoiceOffice");

        } else {
            InvoiceVO invoiceVO2 = new InvoiceVO();
            invoiceVO2.setInvoiceOffice(invoiceVO.getInvoiceOffice());
//            invoiceVO2.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
//            invoiceVO2.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
            invoiceVO2.setCommonInvoiceAmount(sumCommonInvoiceAmount);
            invoiceVO2.setCommonNoTaxAmount(sumCommonNoTaxAmount);
            invoiceVO2.setTotalThisYearInvoiceAmount(sumThisYearInvoiceAmount);
            invoiceVO2.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
            list.add(invoiceVO2);
        }
        invoiceVO1.setInvoiceOffice("合计");


//        invoiceVO1.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
//        invoiceVO1.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
        invoiceVO1.setCommonInvoiceAmount(sumCommonInvoiceAmount);
        invoiceVO1.setCommonNoTaxAmount(sumCommonNoTaxAmount);
        invoiceVO1.setTotalThisYearInvoiceAmount(sumThisYearInvoiceAmount);
        invoiceVO1.setTotalThisYearNoTaxAmount(sumThisYearNoTaxAmount);
        list.add(invoiceVO1);
        map.put("list", list);
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计汇总按开票单位统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


    private void setCreateLimitPart(List<InvoiceVO> invoiceVOS) {
        invoiceVOS.forEach(invoiceVO1 -> {
            String createLimitPart = "3个月".equals(invoiceVO1.getCreditLimit()) ? "企业" : "政府";
            invoiceVO1.setCreateLimitPart(createLimitPart);
        });
    }


    public List<InvoiceVO> getReceiptGatherStatistics(List<InvoiceVO> list, Map<String, List<InvoiceVO>> map, Map<String, List<InvoiceVO>> thisYearMap, String condition) {
        //   List<InvoiceVO> list = new ArrayList<>();
        map.forEach((key, list1) -> {
            log.info(key);

            InvoiceVO in = new InvoiceVO();

            if ("contractUser".equals(condition)) {
                in.setContractUser(key);
            } else if ("departmentName".equals(condition)) {
                in.setDepartmentName(key);
            } else if ("invoiceDateTime".equals(condition)) {
                in.setInvoiceDateTime(key);
            } else if ("invoiceOffice".equals(condition)) {
                in.setInvoiceOffice(key);
            }

            List<InvoiceVO> specialList = list1.stream().filter(invoiceVO1 -> "专".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
            List<InvoiceVO> commonList = list1.stream().filter(invoiceVO1 -> "普".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());

            Double specialInvoiceAmount = getInvoiceAmount(specialList);
            in.setSpecialInvoiceAmount(specialInvoiceAmount);

            Double specialNoTaxAmount = getNoTaxAmount(specialList);
            in.setSpecialNoTaxAmount(specialNoTaxAmount);
            Double commonInvoiceAmount = getInvoiceAmount(commonList);
            in.setCommonInvoiceAmount(commonInvoiceAmount);
            Double commonNoTaxAmount = getNoTaxAmount(commonList);
            in.setCommonNoTaxAmount(commonNoTaxAmount);

            Double invoiceAmount = getInvoiceAmount(list1);
            in.setTotalInvoiceAmount(invoiceAmount);
            Double noTaxAmount = getNoTaxAmount(list1);
            in.setTotalNoTaxAmount(noTaxAmount);


            thisYearMap.forEach((key2, list2) -> {
                if (key.equals(key2)) {
                    Double thisYearInvoiceAmount = getInvoiceAmount(list2);
                    Double thisYearNoTaxAmount = getNoTaxAmount(list2);
                    in.setTotalThisYearNoTaxAmount(thisYearNoTaxAmount);
                    in.setTotalThisYearInvoiceAmount(thisYearInvoiceAmount);
                }
            });

            list.add(in);
        });
        return list;
    }

    public Double getInvoiceAmount(List<InvoiceVO> list) {
        return  DoubleUtil.getExactDouble(list.stream().mapToDouble(value -> value.getInvoiceAmount()).sum());
    }


    public Double getNoTaxAmount(List<InvoiceVO> list) {
        return DoubleUtil.getExactDouble(list.stream().mapToDouble(value -> value.getNoTaxAmount()).sum());
    }

    @GetMapping("/gather/test")
    public void test(InvoiceVO invoiceVO, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.receiptGatherStatistics(invoiceVO);


        invoiceVOS.forEach(invoiceVO2 -> {
            String dateString = DateUtils.date2String(invoiceVO2.getInvoiceDate());
            invoiceVO2.setInvoiceDateTime(dateString);
        });
        Map<String, List<InvoiceVO>> collect1 = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getDepartmentName));


        Map<String, Object> value = new HashMap<String, Object>();
        List<Map<String, Object>> valList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> colList = new ArrayList<Map<String, Object>>();

        collect1.forEach((key, list3) -> {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("time", key);
            map.put("invoiceCount", "开票量");
            map.put("payed", "已回款");
            map.put("totalInvoiceAmount", "t.total" + key);
            map.put("receiveTotalInvoice", "t.receive" + key);
            colList.add(map);

        });

        Map<String, List<InvoiceVO>> devCollect = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getDepartmentName));

        devCollect.forEach((dep, depList) -> {
            Map<String, Object> depMap = new HashMap<String, Object>();
            depMap.put("depName", dep);
            collect1.forEach((key, l) -> {
                double totalInvoiceAmount = depList.stream().filter(i -> key.equals(i.getInvoiceDateTime())).mapToDouble(Invoice::getInvoiceAmount).sum();
                double totalReceivedAmount = depList.stream().filter(i -> key.equals(i.getInvoiceDateTime())).mapToDouble(Invoice::getReceivedAmount).sum();
                depMap.put("total" + key, totalInvoiceAmount);
                depMap.put("receive" + key, totalReceivedAmount);
            });
            valList.add(depMap);
        });

    }

    @GetMapping("/gather/test2")
    public void test2(InvoiceVO invoiceVO, HttpServletResponse response) throws Exception{
        List<InvoiceVO> invoiceVOS = invoiceService.receiptGatherStatistics(invoiceVO);
        invoiceVOS.forEach(invoiceVO2 -> {
            String dateString = DateUtils.date2String(invoiceVO2.getInvoiceDate());
            invoiceVO2.setInvoiceDateTime(dateString);
        });

        Map<String, List<InvoiceVO>> collect1 = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getDepartmentName));

        Map<String, List<InvoiceVO>> collect2 = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceDateTime));

        List<String> times = new ArrayList<>();
        collect2.forEach((key2,list2) ->{
            times.add(key2);

        });

        collect1.forEach((key,list) ->{



        });
    }


}
