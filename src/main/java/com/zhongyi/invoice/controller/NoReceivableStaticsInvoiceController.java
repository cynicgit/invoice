package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.*;
import com.zhongyi.invoice.service.InvoiceService;
import com.zhongyi.invoice.style.MyExcelExportStylerDefaultImpl;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class NoReceivableStaticsInvoiceController {


    @Value("${excelPath}")
    private String excelPath;

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
    @OperateLog("未回款明细导出")
    public void exportExcel(InvoiceVO invoiceVO, int type, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.noReceiveAmount(invoiceVO);
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        String name = "未回款明细-";
        List<InvoiceVO> collect = null;
        if (type == 0) {
            name += "部门";
            collect = invoiceVOS.stream().filter(i -> StringUtils.isEmpty(invoiceVO.getCondition()) || i.getDepartmentName().contains(invoiceVO.getCondition())).collect(Collectors.toList());
        } else if (type == 2) {
            name += "信用";
            collect = invoiceVOS.stream().filter(i -> StringUtils.isEmpty(invoiceVO.getCondition()) ||  i.getCreditLimit().contains(invoiceVO.getCondition())).collect(Collectors.toList());
        } else if (type == 3) {
            name += "开票";
            collect = invoiceVOS.stream().filter(i -> StringUtils.isEmpty(invoiceVO.getCondition()) || i.getInvoiceOffice().contains(invoiceVO.getCondition())).collect(Collectors.toList());
        } else if (type == 4) {
            name += "项目负责人";
            collect = invoiceVOS.stream().filter(i -> StringUtils.isEmpty(invoiceVO.getCondition()) || i.getContractUser().contains(invoiceVO.getCondition())).collect(Collectors.toList());
        }
        exportParams.setTitle(name + " " + invoiceVO.getStartDate() + "-" + invoiceVO.getEndDate());
        EasyPoiUtils.defaultExport(collect, Invoice.class, name +".xlsx", response, exportParams);
    }

    /**
     * 未回款统计汇总
     *
     * @param invoiceVO
     * @param response
     * @throws IOException
     */
    @GetMapping("/no_receiver_invoice_summary")
    @OperateLog("未回款汇总导出")
    public void exportExcelNoAmountStatistics(InvoiceVO invoiceVO, @RequestParam(required = true) int type, HttpServletResponse response) throws IOException {
        List<ReceivableStaticsInvoice> invoiceVOS = invoiceService.getInvoices(invoiceVO.getStartDate(), invoiceVO.getEndDate());
        Map<String, Object> map = null;
        if (type == 1) {
            ExportNoReceiver exportNoReceiver1 = new ExportNoReceiver();
            exportNoReceiver1.setKey(invoiceVO.getStartDate() + "-" + invoiceVO.getEndDate());
            Double invoiceAmount = invoiceVOS.stream().mapToDouble(Invoice::getInvoiceAmount).sum();
            Double noReceive = invoiceVOS.stream().mapToDouble(Invoice::getNoReceivedAmount).sum();
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
            map = getInvoiceMap(invoiceVOS, type, invoiceVO);
        }
        TemplateExportParams params = new TemplateExportParams();
        String name = "未回款汇总-";
        if (type == 0) { // 部门
            name += "部门";
            params.setTemplateUrl(excelPath + "noReceiveAmountDep.xlsx");
        } else if (type == 1) { // 日期
            params.setTemplateUrl(excelPath + "noReceiveAmountDate.xlsx");
        } else if (type == 2) { // 信用
            name += "信用";
            params.setTemplateUrl(excelPath + "noReceiveAmountCreadt.xlsx");
        } else if (type == 3) { // 开票
            name += "开票";
            params.setTemplateUrl(excelPath +  "noReceiveAmountOffice.xlsx");
        } else if (type == 4) { // 项目
            name += "项目负责人";
            params.setTemplateUrl(excelPath + "noReceiveAmountUser.xlsx");
        }

        params.setStyle(MyExcelExportStylerDefaultImpl.class);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(name + ".xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    private Map<String, Object> getInvoiceMap(List<ReceivableStaticsInvoice> invoiceVOS, int type, InvoiceVO invoiceVO) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, List<ReceivableStaticsInvoice>> collect = null;
        if (type == 0) {
            collect = invoiceVOS.stream().filter(i -> StringUtils.isEmpty(invoiceVO.getCondition()) || i.getDepartmentName().contains(invoiceVO.getCondition())).collect(Collectors.groupingBy(ReceivableStaticsInvoice::getDepartmentName));
        } else if (type == 2) {
            collect = invoiceVOS.stream().filter(i -> StringUtils.isEmpty(invoiceVO.getCondition()) ||  i.getCreditLimit().contains(invoiceVO.getCondition())).collect(Collectors.groupingBy(ReceivableStaticsInvoice::getCreditLimit));
        } else if (type == 3) {
            collect = invoiceVOS.stream().filter(i -> StringUtils.isEmpty(invoiceVO.getCondition()) || i.getInvoiceOffice().contains(invoiceVO.getCondition())).collect(Collectors.groupingBy(ReceivableStaticsInvoice::getInvoiceOffice));
        } else if (type == 4) {
            collect = invoiceVOS.stream().filter(i -> StringUtils.isEmpty(invoiceVO.getCondition()) || i.getContractUser().contains(invoiceVO.getCondition())).collect(Collectors.groupingBy(ReceivableStaticsInvoice::getContractUser));
        }

        List<ExportNoReceiver> list = new ArrayList<>();
        collect.forEach((key, list1) -> {
            log.info(key);
            ExportNoReceiver exportNoReceiver = new ExportNoReceiver();
            exportNoReceiver.setKey(key);
            if (type == 2) {
                exportNoReceiver.setKeyDesc(key.equals(Const.CreditLimit3) ? Const.CreditTypeOffice : Const.CreditTypeOrg);
            }

            Double invoiceAmount = list1.stream().mapToDouble(ReceivableStaticsInvoice::getInvoiceAmount).sum();
            Double noReceive = list1.stream().mapToDouble(ReceivableStaticsInvoice::getNoReceivedAmount).sum();
            exportNoReceiver.setTotalInvoice(invoiceAmount);
            exportNoReceiver.setNoReceiveTotalInvoice(noReceive);
            list.add(exportNoReceiver);
        });
        final Double[] sumInvoice = {0.0};
        final Double[] sumNoReceiveInvoice = {0.0};
        collect.values().forEach(lists -> {
            sumInvoice[0] = sumInvoice[0] + lists.stream().mapToDouble(ReceivableStaticsInvoice::getInvoiceAmount).sum();
            sumNoReceiveInvoice[0] = sumNoReceiveInvoice[0] + lists.stream().mapToDouble(ReceivableStaticsInvoice::getNoReceivedAmount).sum();
        });

        ExportNoReceiver exportNoReceiver = new ExportNoReceiver();
        exportNoReceiver.setKey("合计");
        exportNoReceiver.setTotalInvoice(sumInvoice[0]);
        exportNoReceiver.setNoReceiveTotalInvoice(sumNoReceiveInvoice[0]);
        list.add(exportNoReceiver);
        map.put("list", list);
        map.put("sumInvoice", sumInvoice[0]);
        map.put("sumNoReceiveInvoice", sumNoReceiveInvoice);
        return map;
    }

}
