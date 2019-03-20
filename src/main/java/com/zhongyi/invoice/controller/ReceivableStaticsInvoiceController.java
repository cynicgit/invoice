package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.zhongyi.invoice.entity.*;
import com.zhongyi.invoice.service.InvoiceService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReceivableStaticsInvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Value("${excelPath}")
    private String excelPath;

    @GetMapping("/receivable_statics_invoice")
    public void ReceivableStaticsInvoice(String startDate, String endDate, HttpServletResponse response) throws Exception {
        List<ReceivableStaticsInvoice> list = invoiceService.getInvoices(startDate, endDate);
        final double[] sum = {0.00f};
        list.forEach(i -> {
            i.setNoReceivedAmount(i.getInvoiceAmount() - i.getReceivedAmount());
            sum[0] = sum[0] + i.getNoReceivedAmount();
            int monthBetween = DayCompare.getMonthBetween(i.getInvoiceDate(), new Date());
            if (monthBetween < 6 && monthBetween >= 3) {
                i.setLimitAmount1(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween >= 6 && monthBetween < 9) {
                i.setLimitAmount2(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween > 9 && monthBetween <= 12) {
                i.setLimitAmount3(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween > 12 && monthBetween <= 24) {
                i.setLimitAmount4(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween > 24 && monthBetween <= 36) {
                i.setLimitAmount5(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween > 36){
                i.setLimitAmount6(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            }

        });
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.put("sum", new BigDecimal( sum[0] / 10000).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(excelPath + "receivableStaticsInvoice.xlsx");
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("应收账款账龄分析明细.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


    @GetMapping("/receivable_statics_invoice_summary")
    public void ReceivableStaticsInvoiceSummary(String startDate, String endDate, HttpServletResponse response) throws Exception {
        List<ReceivableStaticsInvoice> list = invoiceService.getInvoices(startDate, endDate);
        double sum = 0.00f;
        double summary1 = 0.00f;
        double summary2 = 0.00f;
        double summary3 = 0.00f;
        double summary4 = 0.00f;
        double summary5 = 0.00f;
        double summary6 = 0.00f;
        for (ReceivableStaticsInvoice i : list) {
            i.setNoReceivedAmount(i.getInvoiceAmount() - i.getReceivedAmount());
            sum = sum + i.getNoReceivedAmount();
            int monthBetween = DayCompare.getMonthBetween(i.getInvoiceDate(), new Date());
            if (monthBetween < 6 && monthBetween >= 3) {
                summary1 = summary1 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween >= 6 && monthBetween < 9) {
                summary2 = summary2 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween > 9 && monthBetween <= 12) {
                summary3 = summary3 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween > 12 && monthBetween <= 24) {
                summary4 = summary4 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween > 24 && monthBetween <= 36) {
                summary5 = summary5 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween > 36){
                summary6 = summary6 + (i.getInvoiceAmount() - i.getReceivedAmount());
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sum", new BigDecimal( sum / 10000).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("summary1", new BigDecimal( summary1 / 10000).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("summary2", new BigDecimal( summary2 / 10000).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("summary3", new BigDecimal( summary3 / 10000).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("summary4", new BigDecimal( summary4 / 10000).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("summary5", new BigDecimal( summary5 / 10000).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("summary6", new BigDecimal( summary6 / 10000).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(excelPath + "receivableStaticsInvoiceSummary.xlsx");
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("应收账款账龄分析汇总.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


}
