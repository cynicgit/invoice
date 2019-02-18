package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.InvoiceVO;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.InvoiceService;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
@RequestMapping("/system/excel")
@Slf4j
public class ExcelController {

    @Autowired
    private InvoiceService invoiceService;


    @PostMapping
    public ZYResponse importExcel(MultipartFile file) {
        Map<String, Object> map = invoiceService.importExcel(file);
        return ZYResponse.success(map);
    }

    /**
     * 未回款详情
     * @param invoiceVO
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportExcelNoAmount")
    public void exportExcel(InvoiceVO invoiceVO, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.noReceiveAmount(invoiceVO);
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        EasyPoiUtils.defaultExport(invoiceVOS, Invoice.class,"xxx.xlsx", response, exportParams);
    }

    /**
     * 未回款统计
     * @param invoiceVO
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportExcelNoAmount/statistics")
    public void exportExcelNoAmountStatistics(InvoiceVO invoiceVO, int type, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.noReceiveAmount(invoiceVO);
        Map<String, Object> map = new HashMap<String, Object>();
        if (type == 0) {
            Map<String, List<InvoiceVO>> collect = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getDepartmentName));
            List<InvoiceVO> list = new ArrayList<>();
            collect.forEach((key, list1) -> {
                log.info(key);
                InvoiceVO in = new InvoiceVO();
                in.setDepartmentName(key);
                Double invoiceAmount = list1.stream().mapToDouble(new ToDoubleFunction<InvoiceVO>() {
                    @Override
                    public double applyAsDouble(InvoiceVO value) {
                        if (value.getInvoiceAmount() == null) {
                            log.info(value.getId() + "");
                        }
                        return value.getInvoiceAmount();
                    }
                }).sum();
                Double noReceive = list1.stream().mapToDouble(new ToDoubleFunction<InvoiceVO>() {
                    @Override
                    public double applyAsDouble(InvoiceVO value) {
                        if (value.getNoReceivedAmount() == null) {
                            log.info(value.getId() + "");
                        }
                        return value.getNoReceivedAmount();
                    }
                }).sum();
                in.setTotalInvoice(invoiceAmount);
                in.setNoReceiveTotalInvoice(noReceive);
                list.add(in);
            });
            map.put("maplist", list);
            Double sumInvoice = invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
            Double sumNoReceiveInvoice = invoiceVOS.stream().mapToDouble(InvoiceVO::getNoReceivedAmount).sum();
            map.put("sumInvoice", sumInvoice);
            map.put("sumNoReceiveInvoice", sumNoReceiveInvoice);
            File file = ResourceUtils.getFile("classpath:noReceiveAmountDep.xlsx");
            log.info(file.getAbsolutePath());
            TemplateExportParams params = new TemplateExportParams();
            params.setTemplateUrl("D://noReceiveAmountDep.xlsx");
            Workbook workbook = ExcelExportUtil.exportExcel(params, map);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("部门汇总.xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
        }

    }


}
