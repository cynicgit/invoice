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
import org.apache.commons.collections4.map.LinkedMap;
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
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/17.
 * @Description:
 */
@RequestMapping("/payed")
@RestController
@Slf4j
public class PayedController {

    @Value("${excelPath}")
    private String excelPath;


    @Autowired
    private InvoiceService invoiceService;


    @GetMapping("/detail/dep")
    @OperateLog("已回款明细")
    public void payedDetailByDepId(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setDepartmentName(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedDetail(invoiceVO);
        setCreateLimitPart(invoiceVOS);
        //按部门
//        if (!StringUtils.isEmpty(invoiceVO.getDepartmentName())) {
//            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(Invoice :: getDepartmentName));
//            invoiceVOS = map.get(String.valueOf(invoiceVO.getDepartmentName()));
//        }


        if (!StringUtils.isEmpty(invoiceVO.getDepartmentName())) {
            invoiceVOS = invoiceVOS.stream().filter(invoiceVO2 -> invoiceVO2.getDepartmentName().contains(invoiceVO.getDepartmentName())).collect(Collectors.toList());
        }

        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        String path = excelPath + "receiptDetail.xlsx";
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款明细按部门统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    @GetMapping("/detail/creditLimit")
    @OperateLog("已回款明细")
    public void payedDetailByCreditLimit(InvoiceVO invoiceVO, String condition, HttpServletResponse response, HttpServletRequest request) throws IOException {
        invoiceVO.setCreditLimit(condition);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedDetail(invoiceVO);
        setCreateLimitPart(invoiceVOS);
        //按信用日期
//        if (!StringUtils.isEmpty(invoiceVO.getCreditLimit())) {
//            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getCreditLimit));
//            invoiceVOS = map.get(invoiceVO.getCreditLimit());
//
//        }

        if (!StringUtils.isEmpty(invoiceVO.getCreditLimit())) {
            invoiceVOS = invoiceVOS.stream().filter(invoiceVO2 -> invoiceVO2.getCreditLimit().contains(invoiceVO.getCreditLimit())).collect(Collectors.toList());
        }

        if (user.getType() == 2) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getContractUser()));
            invoiceVOS = map.get(user.getName());
        }
        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        String path = excelPath + "receiptDetail.xlsx";
        // String path =    "static/excel/receiptDetail.xlsx";
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款明细按信用日期统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


    @GetMapping("/detail/invoiceOffice")
    @OperateLog("已回款明细")
    public void payedDetailByInvoiceOffice(InvoiceVO invoiceVO, String condition, HttpServletResponse response, HttpServletRequest request) throws IOException {

        invoiceVO.setInvoiceOffice(condition);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedDetail(invoiceVO);
        setCreateLimitPart(invoiceVOS);
//        if (!StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
//            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceOffice));
//            invoiceVOS = map.get(invoiceVO.getInvoiceOffice());
//        }

        if (!StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
            invoiceVOS = invoiceVOS.stream().filter(invoiceVO2 -> invoiceVO2.getInvoiceOffice().contains(invoiceVO.getInvoiceOffice())).collect(Collectors.toList());
        }

        if (user.getType() == 2) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getContractUser()));
            invoiceVOS = map.get(user.getName());
        }
        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        String path = excelPath + "receiptDetail.xlsx";
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款明细按开票单位统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    @GetMapping("/detail/contractUser")
    @OperateLog("已回款明细")
    public void payedDetailByContractUser(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setContractUser(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedDetail(invoiceVO);
        setCreateLimitPart(invoiceVOS);
        //按项目负责人
//        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
//            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
//            invoiceVOS = map.get(invoiceVO.getContractUser());
//        }
        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            invoiceVOS = invoiceVOS.stream().filter(invoiceVO2 -> invoiceVO2.getContractUser().contains(invoiceVO.getContractUser())).collect(Collectors.toList());
        }
        Map<String, Object> mapParms = new HashMap<>();
        mapParms.put("list", invoiceVOS);
        String path = excelPath + "receiptDetail.xlsx";

        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款明细按开票单位统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


    @GetMapping("/gather/invoiceOffice")
    @OperateLog("已回款汇总")
    public void payedGatherByInvoiceOffice(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setInvoiceOffice(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedGather(invoiceVO);

        if (!StringUtils.isEmpty(invoiceVO.getInvoiceOffice())) {
            invoiceVOS = invoiceVOS.stream().filter(invoiceVO2 -> invoiceVO2.getInvoiceOffice().contains(invoiceVO.getInvoiceOffice())).collect(Collectors.toList());
        }
        List<InvoiceVO> list = new ArrayList<>();
        Map<String, Object> mapParms = new HashMap<>();
        String path = excelPath + "payedGatherInvoiceOff.xlsx";
        Double sumInvoice = invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();

        Double sumReceivedAmount = invoiceVOS.stream().mapToDouble(InvoiceVO::getReceivedAmount).sum();

        InvoiceVO invoiceVO1 = new InvoiceVO();


        Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceOffice));

        if (map.size() == 1) {
            InvoiceVO invoiceVO2 = new InvoiceVO();
            // invoiceVO2.setInvoiceOffice(invoiceVOS.get(0).getInvoiceOffice());
            invoiceVO2.setTotalInvoice(sumInvoice);
            invoiceVO2.setReceiveTotalInvoice(sumReceivedAmount);
            list.add(invoiceVO2);
        } else {
            list = getPayedGatherStatistics(map, "invoiceOffice");
        }

        invoiceVO1.setInvoiceOffice("合计");
        //   invoiceVO1.setTotalInvoice(sumInvoice);
        invoiceVO1.setReceiveTotalInvoice(sumReceivedAmount);
        list.add(invoiceVO1);
        mapParms.put("list", list);

        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款汇总按开票单位统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


    @GetMapping("/gather/contractUser")
    @OperateLog("已回款汇总")
    public void payedGatherByContractUser(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setContractUser(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedGather(invoiceVO);

        if (!StringUtils.isEmpty(invoiceVO.getContractUser())) {
            invoiceVOS = invoiceVOS.stream().filter(invoiceVO2 -> invoiceVO2.getContractUser().contains(invoiceVO.getContractUser())).collect(Collectors.toList());
        }

        List<InvoiceVO> list = new ArrayList<>();
        Map<String, Object> mapParms = new HashMap<>();
        String path = excelPath + "payedGatherConUser.xlsx";
        Double sumInvoice = invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();

        Double sumReceivedAmount = invoiceVOS.stream().mapToDouble(InvoiceVO::getReceivedAmount).sum();

        InvoiceVO invoiceVO1 = new InvoiceVO();

        Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getContractUser));
        if (map.size() == 1) {
            InvoiceVO invoiceVO2 = new InvoiceVO();
            invoiceVO2.setContractUser(invoiceVOS.get(0).getContractUser());
          //  invoiceVO2.setTotalInvoice(sumInvoice);
            invoiceVO2.setReceiveTotalInvoice(sumReceivedAmount);
            list.add(invoiceVO2);
        } else {
            list = getPayedGatherStatistics(map, "contractUser");
        }

//        if (StringUtils.isEmpty(invoiceVO.getContractUser())) {
//
//        } else {
//
//        }
        invoiceVO1.setContractUser("合计");
      //  invoiceVO1.setTotalInvoice(sumInvoice);
        invoiceVO1.setReceiveTotalInvoice(sumReceivedAmount);
        list.add(invoiceVO1);
        mapParms.put("list", list);

        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款汇总按项目负责人统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    @GetMapping("/gather/dep")
    @OperateLog("已回款汇总")
    public void payedGatherByDep(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {


        String path = excelPath + "payedGatherDep.xlsx";
        log.info(path);
        // FileInputStream fileInputStream = new FileInputStream(path);
        invoiceVO.setDepartmentName(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedGather(invoiceVO);

        if (!StringUtils.isEmpty(invoiceVO.getDepartmentName())) {
            invoiceVOS = invoiceVOS.stream().filter(invoiceVO2 -> invoiceVO2.getDepartmentName().contains(invoiceVO.getDepartmentName())).collect(Collectors.toList());
        }


        invoiceVOS.forEach(invoiceVO2 -> {
            String dateString = DateUtils.date2String(invoiceVO2.getInvoiceDate());
            invoiceVO2.setInvoiceDateTime(dateString);
        });


        Map<String, Object> value = new HashMap<String, Object>();
        List<Map<String, Object>> valList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> colList = new ArrayList<Map<String, Object>>();

        Map<String, List<InvoiceVO>> collect1 = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getInvoiceDateTime));

        List<String> keys = collect1.keySet().stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Date date = DateUtils.string2Date(o1);
                Date date1 = DateUtils.string2Date(o2);
                return date.compareTo(date1);
            }
        }).collect(Collectors.toList());


        LinkedMap<String, List<InvoiceVO>> linkedMap = new LinkedMap();
        keys.forEach(key -> {
            linkedMap.put(key, collect1.get(key));
        });

        linkedMap.forEach((key, list3) -> {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("time", key);
          //  map.put("invoiceCount", "开票量");
            map.put("payed", "已回款");
          //  map.put("totalInvoiceAmount", "t.total" + key);
            map.put("receiveTotalInvoice", "t.receive" + key);
            colList.add(map);

        });

        Map<String, List<InvoiceVO>> devCollect = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getDepartmentName));

        devCollect.forEach((dep, depList) -> {
            Map<String, Object> depMap = new HashMap<String, Object>();
            depMap.put("depName", dep);
            linkedMap.forEach((key, l) -> {
                double totalInvoiceAmount = depList.stream().filter(i -> key.equals(i.getInvoiceDateTime())).mapToDouble(Invoice::getInvoiceAmount).sum();
                double totalReceivedAmount = depList.stream().filter(i -> key.equals(i.getInvoiceDateTime())).mapToDouble(Invoice::getReceivedAmount).sum();
               // depMap.put("total" + key, DoubleUtil.getNum(totalInvoiceAmount));
                depMap.put("receive" + key, DoubleUtil.getNum(totalReceivedAmount));
            });
            valList.add(depMap);
        });


        Map<String, Object> depMap2 = new HashMap<String, Object>();
        depMap2.put("depName", "合计");

        linkedMap.forEach((key, list3) -> {
            double sum = list3.stream().mapToDouble(value2 -> value2.getInvoiceAmount()).sum();
            double sum1 = list3.stream().mapToDouble(value2 -> value2.getReceivedAmount()).sum();
           // depMap2.put("total" + key, DoubleUtil.getNum(sum));
            depMap2.put("receive" + key, DoubleUtil.getNum(sum1));

        });
        valList.add(depMap2);


        value.put("colList", colList);
        value.put("valList", valList);
        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        params.setColForEach(true);
        Workbook workbook = ExcelExportUtil.exportExcel(params, value);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款汇总按部门统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    @GetMapping("/gather/creditLimit")
    @OperateLog("已回款汇总")
    public void payedGatherByCreditLimit(InvoiceVO invoiceVO, String condition, HttpServletResponse response) throws IOException {
        invoiceVO.setCreditLimit(condition);
        List<InvoiceVO> invoiceVOS = invoiceService.exportExcelPayedGather(invoiceVO);

        if (!StringUtils.isEmpty(invoiceVO.getCreditLimit())) {
            invoiceVOS = invoiceVOS.stream().filter(invoiceVO2 -> invoiceVO2.getCreditLimit().contains(invoiceVO.getCreditLimit())).collect(Collectors.toList());
        }

        List<InvoiceVO> list = new ArrayList<>();
        Map<String, Object> mapParms = new HashMap<>();
        Double sumInvoice = invoiceVOS.stream().mapToDouble(InvoiceVO::getInvoiceAmount).sum();

        Double sumReceivedAmount = invoiceVOS.stream().mapToDouble(InvoiceVO::getReceivedAmount).sum();

        String path = null;
        path = excelPath + "payedGatherCreateLimit.xlsx";
        InvoiceVO invoiceVO1 = new InvoiceVO();
        if (StringUtils.isEmpty(invoiceVO.getCreditLimit())) {
            Map<String, List<InvoiceVO>> map = invoiceVOS.stream().collect(Collectors.groupingBy(InvoiceVO::getCreditLimit));
            list = getPayedGatherStatistics(map, "creditLimit");

        } else {
            String createLimitPart = null;
            InvoiceVO invoiceVO2 = new InvoiceVO();
            String key = null;
            invoiceVO2.setCreateLimitPart(invoiceVO.getCreditLimit());
            if ("3个月".contains(invoiceVO.getCreditLimit())) {
                createLimitPart = "企业";
                key = "3个月";
            } else if ("6个月".contains(invoiceVO.getCreditLimit())) {
                createLimitPart = "政府";
                key = "6个月";
            } else {
                createLimitPart = "无";
            }

            invoiceVO2.setCreateLimitPart(createLimitPart);
            invoiceVO2.setCreditLimit(key);
         //   invoiceVO2.setTotalInvoice(sumInvoice);
            invoiceVO2.setReceiveTotalInvoice(sumReceivedAmount);
            list.add(invoiceVO2);
        }
        invoiceVO1.setCreateLimitPart("合计");
        invoiceVO1.setCreditLimit("");
       // invoiceVO1.setTotalInvoice(sumInvoice);
        invoiceVO1.setReceiveTotalInvoice(sumReceivedAmount);
        list.add(invoiceVO1);
        mapParms.put("list", list);

        TemplateExportParams params = new TemplateExportParams();
        params.setTemplateUrl(path);
        Workbook workbook = ExcelExportUtil.exportExcel(params, mapParms);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("已回款汇总按信用期限统计.xlsx", "UTF-8"));
        workbook.write(response.getOutputStream());
    }


    private void setCreateLimitPart(List<InvoiceVO> invoiceVOS) {
        invoiceVOS.forEach(invoiceVO1 -> {
            String createLimitPart = "3个月".equals(invoiceVO1.getCreditLimit()) ? "企业" : "政府";
            invoiceVO1.setCreateLimitPart(createLimitPart);
        });
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
            } else if ("invoiceDateTime".equals(condition)) {
                in.setInvoiceDateTime(key);
            }

            Double invoiceAmount = getInvoiceAmount(list1);
            Double receivedAmount = getReceivedAmount(list1);
           // in.setTotalInvoice(invoiceAmount);
            in.setReceiveTotalInvoice(receivedAmount);
            list.add(in);
        });
        return list;
    }

    public Double getInvoiceAmount(List<InvoiceVO> list) {
        return list.stream().mapToDouble(value -> value.getInvoiceAmount()).sum();
    }

    public Double getReceivedAmount(List<InvoiceVO> list) {
        return list.stream().mapToDouble(value -> value.getReceivedAmount()).sum();
    }


}