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



        //专票
        List<InvoiceVO> specialInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "专票".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());

        //普票
        List<InvoiceVO> commonInvoices = invoiceVOS.stream().filter(invoiceVO1 -> "普票".equals(invoiceVO1.getInvoiceType())).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        List<InvoiceVO> specialList;
        List<InvoiceVO> commonList;
        Double sumSpecialInvoiceAmount = null;
        Double sumSpecialNoTaxAmount = null;
        Double sumCommonInvoiceAmount = null;
        Double sumCommonNoTaxAmount = null;
        String path = null;
        File file;
        //按部门统计
        if (invoiceVO.getDepId() != null) {
            Map<String, List<InvoiceVO>> specialMap = specialInvoices.stream().collect(Collectors.groupingBy(invoiceVO2 -> String.valueOf(invoiceVO2.getDepId())));
            specialList = specialMap.get(String.valueOf(invoiceVO.getDepId()));
            //含税金额
            sumSpecialInvoiceAmount = specialList.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
            //不含税金额
            sumSpecialNoTaxAmount = specialList.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();

            Map<String, List<InvoiceVO>> commonMap = commonInvoices.stream().collect(Collectors.groupingBy(invoiceVO2 -> String.valueOf(invoiceVO2.getDepId())));
            commonList = commonMap.get(String.valueOf(invoiceVO.getDepId()));
            //含税金额
            sumCommonInvoiceAmount = commonList.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
            //不含税金额
            sumCommonNoTaxAmount = commonList.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();

            path = "static/excel/receiptGatherDep.xlsx";

        }


        //按开票人
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> specialMap = specialInvoices.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            specialList = specialMap.get(String.valueOf(invoiceVO.getDepId()));
            //含税金额
            sumSpecialInvoiceAmount = specialList.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
            //不含税金额
            sumSpecialNoTaxAmount = specialList.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();

            Map<String, List<InvoiceVO>> commonMap = commonInvoices.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            commonList = commonMap.get(String.valueOf(invoiceVO.getDepId()));
            //含税金额
            sumCommonInvoiceAmount = commonList.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
            //不含税金额
            sumCommonNoTaxAmount = commonList.stream().mapToDouble(InvoiceVO::getNoTaxAmount).sum();

            path = "static/excel/receiptGatherUser.xlsx";

        }

        //按发票性质
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceType()) && !"2".equals(invoiceVO.getInvoiceType())) {
            //含税金额
            sumCommonInvoiceAmount = "专票".equals(invoiceVO.getInvoiceType()) ?
                    specialInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum() :
                    commonInvoices.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();
            //不含税金额
            sumCommonNoTaxAmount = "专票".equals(invoiceVO.getInvoiceType()) ?
                    specialInvoices.stream().mapToDouble(InvoiceVO::getReceivedAmount).sum() :
                    commonInvoices.stream().mapToDouble(InvoiceVO::getReceivedAmount).sum();
            path = "专票".equals(invoiceVO.getInvoiceType()) ? "static/excel/receiptGatherSpecial.xlsx" : "static/excel/receiptGatherCommon.xlsx";
            //  file = ResourceUtils.getFile(path);

        }

        Resource resource = new ClassPathResource(path);
        String filePath = ((ClassPathResource) resource).getPath();
        map.put("list1", specialInvoices);
        map.put("list2", commonInvoices);
        map.put("sumSpecialInvoiceAmount", sumSpecialInvoiceAmount);
        map.put("sumSpecialNoTaxAmount", sumSpecialNoTaxAmount);
        map.put("sumCommonInvoiceAmount", sumCommonInvoiceAmount);
        map.put("sumCommonNoTaxAmount", sumCommonNoTaxAmount);
         System.out.println(1111);
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(filePath);
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
            String createLimitPart = "3个月".equals(invoiceVO1.getCreditLimit())?"企业":"政府";
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
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepId()));
        }

        //按项目负责人
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepId()));

        }

        //按发票类型
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceType()) && !"2".equals(invoiceVO.getInvoiceType())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceType));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepId()));

        }


//        ExportParams exportParams = new ExportParams();
//        exportParams.setType(ExcelType.XSSF);
//        EasyPoiUtils.defaultExport(invoiceVOS, Invoice.class, "xxx.xlsx", response, exportParams);

        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        Resource resource = new ClassPathResource(path);
        String filePath = ((ClassPathResource) resource).getPath();
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(filePath);
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

        String path = "static/excel/receiptDetail.xlsx";
        invoiceVOS.forEach(invoiceVO1 -> {
            String createLimitPart = "3个月".equals(invoiceVO1.getCreditLimit())?"企业":"政府";
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
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepId()));
        }

        //按项目负责人
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepId()));

        }

        //按信用日期
        if (!StringUtils.isEmpty(invoiceVO.getCreditLimit())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getCreditLimit));
            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepId()));

        }

//        ExportParams exportParams = new ExportParams();
//        exportParams.setType(ExcelType.XSSF);
//        EasyPoiUtils.defaultExport(invoiceVOS, Invoice.class, "xxx.xlsx", response, exportParams);

        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        Resource resource = new ClassPathResource(path);
        String filePath = ((ClassPathResource) resource).getPath();
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(filePath);
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
        //按项目负责人统计
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getDepartmentName));
            list = getStatistics(map,"contractUser");
            path = "static/excel/payedGatherConUser.xlsx";
        }

        //按开票单位统计
        if (!StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceOffice));
            list = getStatistics(map,"invoiceOffice");
            path = "static/excel/payedGatherInvoiceOff.xlsx";
        }

        //按开票时间统计
        if (!StringUtils.isEmpty(invoiceVO.getStartDate()) && !StringUtils.isEmpty(invoiceVO.getEndDate())) {

        }

        //按信用期限统计
        if (!StringUtils.isEmpty(invoiceVO.getCreditLimit())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getCreditLimit));
            list = getStatistics(map,"creditLimit");
            path = "static/excel/payedGatherCreateLimit.xlsx";
        }

        //按部门统计
        if (invoiceVO.getDepId() != null) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(invoiceVO2 -> String.valueOf(invoiceVO2.getDepId())));
            list = getStatistics(map,"departmentName");
            path = "static/excel/payedGatherDep.xlsx";
        }
        Double sumInvoice = invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();

        Double sumReceivedAmount = invoiceVOS.stream().mapToDouble(InvoiceVO::getReceivedAmount).sum();

        mapParms.put("list", list);
        mapParms.put("sumInvoice", sumInvoice);
        mapParms.put("sumReceiveInvoice", sumReceivedAmount);
        Resource resource = new ClassPathResource(path);
        String filePath = ((ClassPathResource) resource).getPath();
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(filePath);
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

    public List<InvoiceVO> getStatistics(Map<String, List<InvoiceVO>> map,String condition) {
        List<InvoiceVO> list = new ArrayList<>();
        map.forEach((key, list1) -> {
            log.info(key);

            InvoiceVO in = new InvoiceVO();

            if ("creditLimit".equals(condition)){


                String createLimitPart = "3个月".equals(key)?"企业":"政府";
                in.setCreateLimitPart(createLimitPart);
                in.setCreditLimit(key);
            }else if ("invoiceOffice".equals(condition)){
                in.setInvoiceOffice(key);
            }else if ("contractUser".equals(condition)){
                in.setContractUser(key);
            }else if ("departmentName".equals(condition)){
                in.setDepartmentName(key);
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
