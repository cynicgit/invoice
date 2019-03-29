package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.InvoiceVO;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.InvoiceService;
import com.zhongyi.invoice.utils.DateUtils;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/excel")
@Slf4j
public class ExcelController {

    @Autowired
    private InvoiceService invoiceService;


    @PostMapping
    @OperateLog("导出数据")
    public ZYResponse importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ZYResponse.success("文件为空");
        }

        if (!file.getOriginalFilename().endsWith("xlsx")
                && !file.getOriginalFilename().endsWith("xls")) {
            return ZYResponse.success("文件格式不支持");
        }
        Map<String, Object> map = invoiceService.importExcel(file);
        return ZYResponse.success(map);
    }

    /**
     * 未回款详情
     *
     * @param invoiceVO
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportExcelNoAmount")
    public void exportExcel(InvoiceVO invoiceVO, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.noReceiveAmount(invoiceVO);
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        EasyPoiUtils.defaultExport(invoiceVOS, Invoice.class, "xxx.xlsx", response, exportParams);
    }

    /**
     * 未回款统计
     *
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
                Double invoiceAmount = list1.stream().mapToDouble(value -> value.getInvoiceAmount()).sum();
                Double noReceive = list1.stream().mapToDouble(value -> value.getNoReceivedAmount()).sum();
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


    /**
     * 发票统计汇总
     *
     * @param invoiceVO
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportExcelReceiptGather/statistics")
    public void exportExcelReceiptGather(InvoiceVO invoiceVO, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.receiptGatherStatistics(invoiceVO);


        List<InvoiceVO> list = new ArrayList<>();

        //专票
        List<InvoiceVO> specialInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "专".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());

        //普票
        List<InvoiceVO> commonInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "普".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        Double sumSpecialInvoiceAmount = null;
        Double sumSpecialNoTaxAmount = null;
        Double sumCommonInvoiceAmount = null;
        Double sumCommonNoTaxAmount = null;
        String path = null;
        File file;

        sumSpecialInvoiceAmount = specialInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
        //不含税金额
        sumSpecialNoTaxAmount = specialInvoices.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();

        sumCommonInvoiceAmount = commonInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
        //不含税金额
        sumCommonNoTaxAmount = commonInvoices.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();

        InvoiceVO invoiceVO1 = new InvoiceVO();
        //按部门统计
        if (invoiceVO.getDepId() != null) {
            Map<String, List<InvoiceVO>> depMap = invoiceVOS.stream().collect(Collectors.groupingBy(Invoice::getDepartmentName));

            list = getReceiptGatherStatistics(depMap, "departmentName");

            path = "static/excel/receiptGatherDep.xlsx";
            invoiceVO1.setDepartmentName("合计");

        }


        //按开票人
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> contractUserMap = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));

            list = getReceiptGatherStatistics(contractUserMap, "contractUser");

            path = "static/excel/receiptGatherUser.xlsx";
            invoiceVO1.setContractUser("合计");
        }

        //按发票性质
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceType())) {
            path = "专".equals(invoiceVO.getInvoiceType()) ?
                    "static/excel/receiptGatherSpecial.xlsx" : "static/excel/receiptGatherCommon.xlsx";
            sumCommonInvoiceAmount = "专".equals(invoiceVO.getInvoiceType()) ?
                    getInvoiceAmount(specialInvoices) : getInvoiceAmount(commonInvoices);
            sumCommonNoTaxAmount = "专".equals(invoiceVO.getInvoiceType()) ?
                    getNoTaxAmount(specialInvoices) : getNoTaxAmount(commonInvoices);
            InvoiceVO invoiceVO2 = new InvoiceVO();
            invoiceVO2.setInvoiceType(invoiceVO.getInvoiceType());
            invoiceVO2.setCommonInvoiceAmount(sumCommonInvoiceAmount);
            invoiceVO2.setCommonNoTaxAmount(sumCommonNoTaxAmount);
            list.add(invoiceVO2);
            invoiceVO1.setInvoiceType("合计");
        }

        if (!StringUtils.isEmpty(invoiceVO.getStartDate()) && !StringUtils.isEmpty(invoiceVO.getEndDate())) {
            invoiceVOS.forEach(invoiceVO2 -> {
                String dateString = DateUtils.date2String(invoiceVO2.getInvoiceDate());
                invoiceVO2.setInvoiceDateTime(dateString);
            });
            Map<String, List<InvoiceVO>> invoiceDateTimemap = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceDateTime));
            list = getReceiptGatherStatistics(invoiceDateTimemap, "invoiceDateTime");
            path = "static/excel/receiptGatherTime.xlsx";
            invoiceVO1.setInvoiceDateTime("合计");

//            InvoiceVO invoiceVO2 = new InvoiceVO();
//            invoiceVO2.setInvoiceDateTime(invoiceVO.getStartDate() + "-" + invoiceVO.getEndDate());
//            invoiceVO2.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
//            invoiceVO2.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
//            invoiceVO2.setCommonInvoiceAmount(sumCommonInvoiceAmount);
//            invoiceVO2.setCommonNoTaxAmount(sumCommonNoTaxAmount);
//            list.add(invoiceVO2);
        }

        Resource resource = new ClassPathResource(path);
        String filePath = ((ClassPathResource) resource).getPath();


        invoiceVO1.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
        invoiceVO1.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
        invoiceVO1.setCommonInvoiceAmount(sumCommonInvoiceAmount);
        invoiceVO1.setCommonNoTaxAmount(sumCommonNoTaxAmount);
        list.add(invoiceVO1);
        map.put("list", list);
//        map.put("sumSpecialInvoiceAmount", sumSpecialInvoiceAmount);
//        map.put("sumSpecialNoTaxAmount", sumSpecialNoTaxAmount);
//        map.put("sumCommonInvoiceAmount", sumCommonInvoiceAmount);
//        map.put("sumCommonNoTaxAmount", sumCommonNoTaxAmount);
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计汇总.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());

    }

    /**
     * 发票统计明细
     *
     * @param invoiceVO
     * @param response
     * @throws IOException
     */

    @GetMapping("/exportExcelReceiptDetail/statistics")
    public void exportExcelReceiptDetail(InvoiceVO invoiceVO, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelReceiptDetail(invoiceVO);

        String path = "static/excel/receiptDetail.xlsx";
        invoiceVOS.forEach(invoiceVO1 -> {
            String createLimitPart = "3个月".equals(invoiceVO1.getCreditLimit()) ? "企业" : "政府";
            invoiceVO1.setCreateLimitPart(createLimitPart);
        });
        //按部门
        if (invoiceVO.getDepId() != null) {
            path = "static/excel/receiptDetailDep.xlsx";
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(invoiceVO2 -> String.valueOf(invoiceVO2.getDepId())));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepId()));
        }
        //按开票单位
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceOffice));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getInvoiceOffice()));
        }

        //按项目负责人
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getContractUser()));

        }

        //按发票类型
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceType()) && !"2".equals(invoiceVO.getInvoiceType())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceType));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getInvoiceType()));

        }


        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        Resource resource = new ClassPathResource(path);
        String filePath = ((ClassPathResource) resource).getPath();
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("发票统计明细.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    /**
     * 已回款明细
     *
     * @param invoiceVO
     * @param response
     * @throws IOException
     */

    @GetMapping("/exportExcelPayedDetail/statistics")
    public void exportExcelexportExcelPayed(InvoiceVO invoiceVO, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedDetail(invoiceVO);

        String path = "static/excel/payedGatherDetail.xlsx";
        invoiceVOS.forEach(invoiceVO1 -> {
            String createLimitPart = "3个月".equals(invoiceVO1.getCreditLimit()) ? "企业" : "政府";
            invoiceVO1.setCreateLimitPart(createLimitPart);
        });

        //按部门
        if (invoiceVO.getDepId() != null) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(invoiceVO2 -> String.valueOf(invoiceVO2.getDepId())));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepId()));
        }
        //按开票单位
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceOffice));
            invoiceVOS = map.get(invoiceVO.getInvoiceOffice());
        }

        //按项目负责人
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            invoiceVOS = map.get(invoiceVO.getContractUser());

        }

        //按信用日期
        if (!StringUtils.isEmpty(invoiceVO.getCreditLimit())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getCreditLimit));
            invoiceVOS = map.get(invoiceVO.getCreditLimit());

        }


        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        Resource resource = new ClassPathResource(path);
        String filePath = ((ClassPathResource) resource).getPath();
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款明细.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    /**
     * 已回款汇总
     *
     * @param invoiceVO
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportExcelPayedGather/statistics")
    public void exportExcelPayedGather(InvoiceVO invoiceVO, HttpServletResponse response) throws IOException {
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedGather(invoiceVO);
        List<InvoiceVO> list = new ArrayList<>();
        Map<String, Object> mapParms = new HashMap<>();
        String path = null;
        InvoiceVO invoiceVO1 = new InvoiceVO();
        //按项目负责人统计
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            list = getPayedGatherStatistics(map, "contractUser");
            path = "static/excel/payedGatherConUser.xlsx";
            invoiceVO1.setContractUser("合计");
        }

        //按开票单位统计
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceOffice));
            list = getPayedGatherStatistics(map, "invoiceOffice");
            path = "static/excel/payedGatherInvoiceOff.xlsx";
            invoiceVO1.setInvoiceOffice("合计");
        }

        //按开票时间统计
        if (!StringUtils.isEmpty(invoiceVO.getStartDate()) && !StringUtils.isEmpty(invoiceVO.getEndDate())) {
            invoiceVOS.forEach(invoiceVO2 -> {
                String dateString = DateUtils.date2String(invoiceVO2.getInvoiceDate());
                invoiceVO2.setInvoiceDateTime(dateString);
            });
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceDateTime));
            list = getPayedGatherStatistics(map, "invoiceDateTime");
            path = "static/excel/payedGatherInvoiceOff.xlsx";
            invoiceVO1.setInvoiceDateTime("合计");

//            InvoiceVO invoiceVO2 = new InvoiceVO();
//            invoiceVO2.setInvoiceDateTime(invoiceVO.getStartDate() + "-" + invoiceVO.getEndDate());
//            invoiceVO2.setSpecialInvoiceAmount(sumSpecialInvoiceAmount);
//            invoiceVO2.setSpecialNoTaxAmount(sumSpecialNoTaxAmount);
//            invoiceVO2.setCommonInvoiceAmount(sumCommonInvoiceAmount);
//            invoiceVO2.setCommonNoTaxAmount(sumCommonNoTaxAmount);
//            list.add(invoiceVO2);
        }

        //按信用期限统计
        if (!StringUtils.isEmpty(invoiceVO.getCreditLimit())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getCreditLimit));
            list = getPayedGatherStatistics(map, "creditLimit");
            path = "static/excel/payedGatherCreateLimit.xlsx";
            invoiceVO1.setCreateLimitPart("合计");
            invoiceVO1.setCreditLimit("");
        }

        //按部门统计
        if (invoiceVO.getDepId() != null) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(Invoice::getDepartmentName));
            list = getPayedGatherStatistics(map, "departmentName");
            path = "static/excel/payedGatherDep.xlsx";
            invoiceVO1.setDepartmentName("合计");
        }
        Double sumInvoice = invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();

        Double sumReceivedAmount = invoiceVOS.stream().mapToDouble(InvoiceVO::getReceivedAmount).sum();
        invoiceVO1.setTotalInvoice(sumInvoice);
        invoiceVO1.setReceiveTotalInvoice(sumReceivedAmount);
        list.add(invoiceVO1);
        mapParms.put("list", list);

        Resource resource = new ClassPathResource(path);
        String filePath = ((ClassPathResource) resource).getPath();
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款汇总.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


    public Double getInvoiceAmount(List<InvoiceVO> list) {
        return list.stream().mapToDouble(new ToDoubleFunction<InvoiceVO>() {
            @Override
            public double applyAsDouble(InvoiceVO value) {
                return value.getInvoiceAmount();
            }
        }).sum();
    }


    public Double getNoTaxAmount(List<InvoiceVO> list) {
        return list.stream().mapToDouble(new ToDoubleFunction<InvoiceVO>() {
            @Override
            public double applyAsDouble(InvoiceVO value) {
                return value.getNoTaxAmount();
            }
        }).sum();
    }

    public Double getReceivedAmount(List<InvoiceVO> list) {
        return list.stream().mapToDouble(new ToDoubleFunction<InvoiceVO>() {
            @Override
            public double applyAsDouble(InvoiceVO value) {
                if (value.getNoReceivedAmount() == null) {
                    log.info(value.getId() + "");
                }
                return value.getReceivedAmount();
            }
        }).sum();
    }

    public List<InvoiceVO> getReceiptGatherStatistics(Map<String, List<InvoiceVO>> map, String condition) {
        List<InvoiceVO> list = new ArrayList<>();
        map.forEach((key, list1) -> {
            log.info(key);

            InvoiceVO in = new InvoiceVO();

            if ("contractUser".equals(condition)) {
                in.setContractUser(key);
            } else if ("departmentName".equals(condition)) {
                in.setDepartmentName(key);
            }else if ("invoiceDateTime".equals(condition)){
                in.setInvoiceDateTime(key);
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

            list.add(in);
        });
        return list;
    }

    public List<InvoiceVO> getPayedGatherStatistics(Map<String, List<InvoiceVO>> map, String condition) {
        List<InvoiceVO> list = new ArrayList<>();
        map.forEach((key, list1) -> {
            log.info(key);

            InvoiceVO in = new InvoiceVO();

            if ("creditLimit".equals(condition)) {
                String createLimitPart = "3个月".equals(key) ? "企业" : "政府";
                in.setCreateLimitPart(createLimitPart);
                in.setCreditLimit(key);
            } else if ("invoiceOffice".equals(condition)) {
                in.setInvoiceOffice(key);
            } else if ("contractUser".equals(condition)) {
                in.setContractUser(key);
            } else if ("departmentName".equals(condition)) {
                in.setDepartmentName(key);
            }else if ("invoiceDateTime".equals(condition)){
                in.setInvoiceDateTime(key);
            }

            Double invoiceAmount = getInvoiceAmount(list1);
            Double receivedAmount = getReceivedAmount(list1);
            in.setTotalInvoice(invoiceAmount);
            in.setReceiveTotalInvoice(receivedAmount);
            list.add(in);
        });
        return list;
    }
}
