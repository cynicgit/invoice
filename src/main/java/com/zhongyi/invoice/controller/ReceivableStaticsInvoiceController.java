package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.zhongyi.invoice.entity.*;
import com.zhongyi.invoice.service.InvoiceService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReceivableStaticsInvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/receivable_statics_invoice")
    public void ReceivableStaticsInvoice(String startDate, String endDate, HttpServletResponse response) throws Exception {
        List<ReceivableStaticsInvoice> list = invoiceService.getInvoices(startDate, endDate);
        List<ReceivableStaticsInvoice> collect = list.stream().filter(i -> i.getReceivedAmount() == null || (i.getReceivedAmount() != null
                && i.getReceivedAmount() - i.getInvoiceAmount() < 0))
                .collect(Collectors.toList());
        final double[] sum = {0.00f};
        collect.forEach(i -> {
            i.setNoReceivedAmount(i.getInvoiceAmount() - i.getReceivedAmount());
            sum[0] = sum[0] + i.getNoReceivedAmount();
            DayCompare dayCompare = DayCompare.dayCompare(i.getInvoiceDate(), new Date());
            if (dayCompare.getMonth() <= 12) {
                i.setLimitAmount1(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (dayCompare.getMonth() <= 24) {
                i.setLimitAmount2(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (dayCompare.getMonth() <= 36) {
                i.setLimitAmount3(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else {
                i.setLimitAmount4(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            }
        });
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", collect);
        map.put("sum", String.format("%.2f", sum[0] / 10000));
        File file = ResourceUtils.getFile("classpath:receivableStaticsInvoice.xlsx");
        TemplateExportParams params = new TemplateExportParams();

        params.setTemplateUrl("E:\\ideaProjects\\invoice\\src\\main\\resources\\receivableStaticsInvoice.xlsx");
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
        List<ReceivableStaticsInvoice> collect = list.stream().filter(i -> i.getReceivedAmount() == null || (i.getReceivedAmount() != null
                && i.getReceivedAmount() - i.getInvoiceAmount() < 0))
                .collect(Collectors.toList());
        double sum = 0.00f;
        double summary1 = 0.00f;
        double summary2 = 0.00f;
        double summary3 = 0.00f;
        double summary4 = 0.00f;
        for (ReceivableStaticsInvoice i : collect) {
            i.setNoReceivedAmount(i.getInvoiceAmount() - i.getReceivedAmount());
            sum = sum + i.getNoReceivedAmount();
            DayCompare dayCompare = DayCompare.dayCompare(i.getInvoiceDate(), new Date());
            if (dayCompare.getMonth() <= 12) {
                summary1 = summary1 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (dayCompare.getMonth() <= 24) {
                summary2 = summary2 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (dayCompare.getMonth() <= 36) {
                summary3 = summary3 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else {
                summary4 = summary4 + (i.getInvoiceAmount() - i.getReceivedAmount());
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sum", String.format("%.2f", sum / 10000));
        map.put("summary1", String.format("%.2f", summary1 / 10000));
        map.put("summary2", String.format("%.2f", summary2 / 10000));
        map.put("summary3", String.format("%.2f", summary3 / 10000));
        map.put("summary4", String.format("%.2f", summary4 / 10000));
        File file = ResourceUtils.getFile("classpath:receivableStaticsInvoice.xlsx");
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl("E:\\ideaProjects\\invoice\\src\\main\\resources\\receivableStaticsInvoiceSummary.xlsx");
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("应收账款账龄分析汇总.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


}
