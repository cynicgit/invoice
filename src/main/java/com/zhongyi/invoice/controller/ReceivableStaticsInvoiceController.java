package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.alibaba.fastjson.JSONArray;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.*;
import com.zhongyi.invoice.service.InvoiceService;
import com.zhongyi.invoice.utils.ExcelUtil2;
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
    @OperateLog("应收账款账龄分析明细导出")
    public void ReceivableStaticsInvoice(String startDate, String endDate, HttpServletResponse response) throws Exception {
        List<ReceivableStaticsInvoice> list = invoiceService.getInvoices(startDate, endDate);
        final double[] sum = {0.00f};
        list.forEach(i -> {
            i.setNoReceivedAmount(i.getInvoiceAmount() - i.getReceivedAmount());
            sum[0] = sum[0] + i.getNoReceivedAmount();
            int monthBetween = DayCompare.getMonthBetween(i.getInvoiceDate(), new Date());
            if (monthBetween <= 3) {
                i.setLimitAmount0(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween <= 6) {
                i.setLimitAmount1(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween <= 9) {
                i.setLimitAmount2(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween <= 12) {
                i.setLimitAmount3(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween <= 24) {
                i.setLimitAmount4(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else if (monthBetween <= 36) {
                i.setLimitAmount5(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            } else {
                i.setLimitAmount6(String.valueOf(i.getInvoiceAmount() - i.getReceivedAmount()));
            }

        });


        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(list);

        ExcelUtil2.downloadExcelFile2("应收账款账龄分析明细", ExcelUtil2.getZhanglingHeaderMap(), jsonArray,sum[0] / 10000, response);
    }


    @GetMapping("/receivable_statics_invoice_summary")
    @OperateLog("应收账款账龄分析汇总导出")
    public void ReceivableStaticsInvoiceSummary(String startDate, String endDate, HttpServletResponse response) throws Exception {
        List<ReceivableStaticsInvoice> list = invoiceService.getInvoices(startDate, endDate);
        double sum = 0.00f;
        double summary0 = 0.00f;
        double summary1 = 0.00f;
        double summary2 = 0.00f;
        double summary3 = 0.00f;
        double summary4 = 0.00f;
        double summary5 = 0.00f;
        double summary6 = 0.00f;
        double badAmount = 0.00f;
        for (ReceivableStaticsInvoice i : list) {
            i.setNoReceivedAmount(i.getInvoiceAmount() - i.getReceivedAmount());
            sum = sum + i.getNoReceivedAmount();
            int monthBetween = DayCompare.getMonthBetween(i.getInvoiceDate(), new Date());
            if (monthBetween <= 3) {
                summary0 = summary0 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween <= 6) {
                summary1 = summary1 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween <= 9) {
                summary2 = summary2 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween <= 12) {
                summary3 = summary3 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween <= 24) {
                summary4 = summary4 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else if (monthBetween <= 36) {
                summary5 = summary5 + (i.getInvoiceAmount() - i.getReceivedAmount());
            } else {
                summary6 = summary6 + (i.getInvoiceAmount() - i.getReceivedAmount());
            }
        }
        badAmount = list.stream().mapToDouble(ReceivableStaticsInvoice::getBadAmount).sum();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sum",  sum / 10000);
        map.put("summary0",  summary0 / 10000);
        map.put("summary1", summary1 / 10000);
        map.put("summary2",  summary2 / 10000);
        map.put("summary3",  summary3 / 10000);
        map.put("summary4",  summary4 / 10000);
        map.put("summary5",  summary5 / 10000);
        map.put("summary6",  summary6 / 10000);
        map.put("badAmount",  badAmount / 10000);
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(excelPath + "receivableStaticsInvoiceSummary.xlsx");
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("应收账款账龄分析汇总.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
