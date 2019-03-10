package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.zhongyi.invoice.entity.*;
import com.zhongyi.invoice.service.InvoiceService;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class NoReceivableStaticsInvoiceController {


    @Autowired
    private InvoiceService invoiceService;

    /**
     * 未回款详情
     *
     * @param invoiceVO
     * @param response
     * @throws IOException
     */
    @GetMapping("/no_receiver_invoice")
    public void exportExcel(InvoiceVO invoiceVO, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.noReceiveAmount(invoiceVO);
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        EasyPoiUtils.defaultExport(invoiceVOS, Invoice.class, "xxx.xlsx", response, exportParams);
    }

    /**
     * 未回款统计汇总
     *
     * @param invoiceVO
     * @param response
     * @throws IOException
     */
    @GetMapping("/no_receiver_invoice_summary")
    public void exportExcelNoAmountStatistics(InvoiceVO invoiceVO, @RequestParam(required = true) int type, HttpServletResponse response) throws IOException {
        List<ReceivableStaticsInvoice> invoiceVOS = invoiceService.getInvoices(invoiceVO.getStartDate(), invoiceVO.getEndDate());
        Map<String, Object> map = null;
        if (type == 1) {
            ExportNoReceiver exportNoReceiver1 = new ExportNoReceiver();
            exportNoReceiver1.setKey(invoiceVO.getStartDate() + "-" + invoiceVO.getEndDate());
            Double invoiceAmount = invoiceVOS.stream().mapToDouble(Invoice::getInvoiceAmount).sum();
            Double noReceive = invoiceVOS.stream().filter(i -> i.getReceivedAmount() == null || i.getReceivedAmount() - i.getInvoiceAmount() < 0).mapToDouble(Invoice::getNoReceivedAmount).sum();
            exportNoReceiver1.setTotalInvoice(invoiceAmount);
            exportNoReceiver1.setNoReceiveTotalInvoice(noReceive);
            ExportNoReceiver exportNoReceiver2 = new ExportNoReceiver();
            exportNoReceiver2.setKey("合计");
            exportNoReceiver2.setTotalInvoice(invoiceAmount);
            exportNoReceiver2.setNoReceiveTotalInvoice(noReceive);
            List<ExportNoReceiver> list = new ArrayList<>();
            list.add(exportNoReceiver1);
            list.add(exportNoReceiver2);
            map = new HashMap<>();
            map.put("list", list);
        } else {
            map = getInvoiceMap(invoiceVOS, type);
        }
        TemplateExportParams params = new TemplateExportParams();
        if (type == 0) { // 部门
            params.setTemplateUrl("E:\\ideaProjects\\invoice\\src\\main\\resources\\noReceiveAmountDep.xlsx");
        } else if (type == 1) { // 日期
            params.setTemplateUrl("E:\\ideaProjects\\invoice\\src\\main\\resources\\noReceiveAmountDate.xlsx");
        } else if (type == 2) { // 信用
            params.setTemplateUrl("E:\\ideaProjects\\invoice\\src\\main\\resources\\noReceiveAmountCreadt.xlsx");
        } else if (type == 3) { // 开票
            params.setTemplateUrl("E:\\ideaProjects\\invoice\\src\\main\\resources\\noReceiveAmountOffice.xlsx");
        } else if (type == 4) { // 项目
            params.setTemplateUrl("E:\\ideaProjects\\invoice\\src\\main\\resources\\noReceiveAmountUser.xlsx");
        }
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("部门汇总.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    private Map<String, Object> getInvoiceMap(List<ReceivableStaticsInvoice> invoiceVOS, int type) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, List<ReceivableStaticsInvoice>> collect = null;
        if (type == 0) {
            collect = invoiceVOS.stream().collect(Collectors.groupingBy(ReceivableStaticsInvoice::getDepartmentName));
        } else if (type == 2) {
            collect = invoiceVOS.stream().collect(Collectors.groupingBy(ReceivableStaticsInvoice::getCreditLimit));
        } else if (type == 3) {
            collect = invoiceVOS.stream().collect(Collectors.groupingBy(ReceivableStaticsInvoice::getInvoiceOffice));
        } else if (type == 4) {
            collect = invoiceVOS.stream().collect(Collectors.groupingBy(ReceivableStaticsInvoice::getContractUser));
        }

        List<ExportNoReceiver> list = new ArrayList<>();
        collect.forEach((key, list1) -> {
            log.info(key);
            ExportNoReceiver exportNoReceiver = new ExportNoReceiver();
            exportNoReceiver.setKey(key);
            if (type == 2) {
                exportNoReceiver.setKeyDesc(key.equals(Const.CreditLimit3) ? Const.CreditTypeOffice : Const.CreditTypeOrg);
            }

            Double invoiceAmount = list1.stream().mapToDouble(Invoice::getInvoiceAmount).sum();
            Double noReceive = list1.stream().filter(i -> i.getReceivedAmount() == null || i.getReceivedAmount() - i.getInvoiceAmount() < 0).mapToDouble(Invoice::getNoReceivedAmount).sum();
            exportNoReceiver.setTotalInvoice(invoiceAmount);
            exportNoReceiver.setNoReceiveTotalInvoice(noReceive);
            list.add(exportNoReceiver);
        });
        Double sumInvoice = invoiceVOS.stream().mapToDouble(ReceivableStaticsInvoice::getInvoiceAmount).sum();
        Double sumNoReceiveInvoice = invoiceVOS.stream().mapToDouble(ReceivableStaticsInvoice::getNoReceivedAmount).sum();
        ExportNoReceiver exportNoReceiver = new ExportNoReceiver();
        exportNoReceiver.setKey("合计");
        exportNoReceiver.setTotalInvoice(sumInvoice);
        exportNoReceiver.setNoReceiveTotalInvoice(sumNoReceiveInvoice);
        list.add(exportNoReceiver);
        map.put("list", list);
        map.put("sumInvoice", sumInvoice);
        map.put("sumNoReceiveInvoice", sumNoReceiveInvoice);
        return map;
    }

}