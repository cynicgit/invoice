package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.alibaba.fastjson.JSONArray;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.*;
import com.zhongyi.invoice.service.GroupService;
import com.zhongyi.invoice.service.InvoiceService;
import com.zhongyi.invoice.service.TargetService;
import com.zhongyi.invoice.service.UserService;
import com.zhongyi.invoice.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system")
public class ChanZhiController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private TargetService targetService;


    @Autowired
    private UserService userService;

    @GetMapping("/chanzhi/user")
    @OperateLog("商务人员产值统计")
    public void getChanzhi(String startDate, String endDate, HttpServletResponse response, HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        List<ReceivableStaticsInvoice> invoices = invoiceService.getInvoices(startDate, endDate);
        invoices.forEach(i -> i.setInvoiceAmount(i.getInvoiceAmount() / 10000));
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("groupName", "组名");
        headerMap.put("name", "商务人员");
        List<Map<String, Object>> value = new ArrayList<>();
        List<User> allUser = userService.getAllUser();
        JSONArray ja = new JSONArray();
        List<Group> group = groupService.getGroup("");
        List<Target> targets = targetService.getAllUserTarget(endDate.split("-")[0]);
        List<Target> groupTargets = targetService.getAllGroupTarget(endDate.split("-")[0]);

        Group otherGroup = getOtherUser(allUser, group);
        group.add(otherGroup);



        Integer endMonth = Integer.valueOf(endDate.split("-")[1]);
        List<String> jidu2 = getJidu2(startDate, endDate);
        List<Integer> jidu = getJidu(startDate, endDate);
        jidu2.forEach(j -> headerMap.put(j, j));
        headerMap.put("累计开票","累计开票");
        headerMap.put("个人目标产值","个人目标产值");
        headerMap.put("个人累计完成率","个人累计完成率");
        headerMap.put("小组合计","小组合计");
        headerMap.put("小组目标产值","小组目标产值");
        headerMap.put("参考累计完成率","参考累计完成率");
        Map<String, Object> hejiMap = new LinkedHashMap<>();
        hejiMap.put("groupName", "");
        hejiMap.put("name", "合计");
        double leiji = 0.0;
        for (Integer i = 0; i < jidu.size(); i++) {
            Integer j = jidu.get(i);
            if (i + 1 == jidu.size()) {
                Double endMonthInvoice = 0.0;
                for (ReceivableStaticsInvoice invoice: invoices) {
                    Date date = invoice.getInvoiceDate();
                    int month = date.getMonth() + 1;
                    if (month == endMonth) {
                        endMonthInvoice = invoice.getInvoiceAmount() + endMonthInvoice;
                    }
                }
                hejiMap.put(endMonth + "月份", endMonthInvoice);
            }
            double sum = invoices.stream().filter(invoice -> getJi(invoice.getInvoiceDate().getMonth() + 1) == j).mapToDouble(Invoice::getInvoiceAmount).sum();
            leiji = sum + leiji;
            hejiMap.put(j + "季度", sum);
        }
        hejiMap.put("累计开票", leiji);
        double targetSum = targets.stream().mapToDouble(Target::getTarget).sum();
        hejiMap.put("个人目标产值", targetSum);
        if (targetSum > 0) {
            hejiMap.put("个人累计完成率", leiji  / targetSum * 100);
        } else {
            hejiMap.put("个人累计完成率", "");
        }
        final double[] hejixiaozuleiji = {0.0};
        ja.add(hejiMap); // 合计

        group.forEach(g -> {
            List<Map<String,Object>> groupList = new ArrayList<>();
            Map<String, Object> xiaojiMap = new LinkedHashMap<>();
            xiaojiMap.put("groupName", "");
            xiaojiMap.put("name", "小计");
            final double[] xiaojiGerren = {0.0};
            Optional<Target> target = groupTargets.stream().filter(t -> g.getName().equals(t.getName())).findAny();
            g.getUsers().forEach(u -> {
                Map<String, Object> groupMap = new LinkedHashMap<>();
                groupMap.put("groupName", g.getName());
                List<ReceivableStaticsInvoice> collect = invoices.stream().filter(invoice -> invoice.getContractUser().equals(u.getName())).collect(Collectors.toList());
                groupMap.put("name", u.getName());
                for (Integer i = 0; i < jidu.size(); i++) {
                    Integer j = jidu.get(i);
                    if (i + 1 == jidu.size()) {
                        Double endMonthInvoice = 0.0;
                        for (ReceivableStaticsInvoice invoice: collect) {
                            Date date = invoice.getInvoiceDate();
                            int month = date.getMonth() + 1;
                            if (month == endMonth) {
                                endMonthInvoice = invoice.getInvoiceAmount() + endMonthInvoice;
                            }
                        }
                        groupMap.put(endMonth + "月份", endMonthInvoice);
                        Double d = xiaojiMap.get(endMonth + "月份") == null ? 0.0 : (Double) xiaojiMap.get(endMonth + "月份");
                        xiaojiMap.put(endMonth + "月份", d + endMonthInvoice);
                    }
                    double sum = collect.stream().filter(invoice -> getJi(invoice.getInvoiceDate().getMonth() + 1) == j).mapToDouble(Invoice::getInvoiceAmount).sum();

                    groupMap.put(j + "季度", sum);
                    Double d = xiaojiMap.get(j + "季度") == null ? 0.0 : (Double) xiaojiMap.get(j + "季度");
                    xiaojiMap.put(j + "季度", d + sum);
                }
                double groupLeijiSum = collect.stream().mapToDouble(Invoice::getInvoiceAmount).sum();
                groupMap.put("累计开票", groupLeijiSum);
                Optional<Target> userTarget = targets.stream().filter(t -> t.getName().equals(u.getName())).findFirst();
                if (userTarget.isPresent()) {
                    xiaojiGerren[0] = xiaojiGerren[0] + userTarget.get().getTarget();
                    groupMap.put("个人目标产值", userTarget.get().getTarget());
                    groupMap.put("个人累计完成率", groupLeijiSum  / userTarget.get().getTarget()* 100 );
                } else {
                    groupMap.put("个人目标产值", "");
                    groupMap.put("个人累计完成率", "");
                }
                groupList.add(groupMap);
            });

            final Double[] xiaojiLeiji = {0.0};
            xiaojiMap.forEach((key, v) -> {
                if (key.contains("季度")) {
                    xiaojiLeiji[0] = xiaojiLeiji[0] + (Double) v;
                }
            });
            hejixiaozuleiji[0] = hejixiaozuleiji[0] + xiaojiLeiji[0];
            xiaojiMap.put("累计开票",xiaojiLeiji[0]);
            xiaojiMap.put("个人目标产值", xiaojiGerren[0] == 0 ? "" :  xiaojiGerren[0]);
            if ( xiaojiGerren[0] > 0) {
                xiaojiMap.put("个人累计完成率", (Double) xiaojiMap.get("累计开票") / xiaojiGerren[0] * 100);
            } else {
                xiaojiMap.put("个人累计完成率", "");
            }
            if (!"其他".equals(g.getName())) {
                xiaojiMap.put("小组合计",xiaojiLeiji[0]);
            } else {
                xiaojiMap.put("小组合计", "");
            }

            if (target.isPresent()) {
                xiaojiMap.put("小组目标产值", target.get().getTarget());
                xiaojiMap.put("参考累计完成率", xiaojiLeiji[0] / target.get().getTarget() * 100);
            } else {
                xiaojiMap.put("小组目标产值", "");
                xiaojiMap.put("参考累计完成率", "");
            }
            groupList.forEach(map -> {
                map.put("小组合计",xiaojiMap.get("小组合计"));
                map.put("小组目标产值",xiaojiMap.get("小组目标产值"));
                map.put("参考累计完成率",xiaojiMap.get("参考累计完成率"));
            });



            ja.addAll(groupList);
            ja.add(xiaojiMap);
        });
        hejiMap.put("小组合计", hejixiaozuleiji[0]);
        double sum = groupTargets.stream().mapToDouble(Target::getTarget).sum();
        hejiMap.put("小组目标产值", sum);
        hejiMap.put("参考累计完成率",hejixiaozuleiji[0] / sum * 100);

        ExcelUtil.downloadExcelFile("商务人员产值统计表", "期间：" + startDate + "--" + endDate, headerMap,ja, response, new int[]{0, headerMap.size() - 3, headerMap.size() - 2, headerMap.size() - 1});

    }

    @GetMapping("/chanzhi/dep")
    @OperateLog("部门产值统计")
    public void getChanzhiDep(String startDate, String endDate, HttpServletResponse response) {
        List<ReceivableStaticsInvoice> invoices = invoiceService.getInvoices(startDate, endDate);
        invoices.forEach(i -> i.setInvoiceAmount(i.getInvoiceAmount() / 10000));
        Map<String, String> headerMap = new LinkedHashMap<>();
        List<String> jidu2 = getJidu2(startDate, endDate);
        headerMap.put("部门","部门");
        jidu2.forEach(j -> headerMap.put(j, j));
        headerMap.put("累计开票","累计开票");
        Map<String, List<ReceivableStaticsInvoice>> collect = invoices.stream().collect(Collectors.groupingBy(ReceivableStaticsInvoice::getDepartmentName));
        List<Integer> jidu = getJidu(startDate, endDate);
        Integer endMonth = Integer.valueOf(endDate.split("-")[1]);
        Map<String, Object> xiaojiMap = new LinkedHashMap<>();
        final double[] xiaojiDep = {0.0};
        JSONArray ja = new JSONArray();
        collect.forEach((depName, invoiceList) -> {
            Map<String, Object> depMap = new LinkedHashMap<>();
            depMap.put("部门", depName);
            double leijiDep = 0.0;
            for (Integer i = 0; i < jidu.size(); i++) {
                Integer j = jidu.get(i);
                if (i + 1 == jidu.size()) {
                    Double endMonthInvoice = 0.0;
                    for (ReceivableStaticsInvoice invoice: invoiceList) {
                        Date date = invoice.getInvoiceDate();
                        int month = date.getMonth() + 1;
                        if (month == endMonth) {
                            endMonthInvoice = invoice.getInvoiceAmount() + endMonthInvoice;
                        }
                    }
                    depMap.put(endMonth + "月份", endMonthInvoice);
                    Double d = xiaojiMap.get(endMonth + "月份") == null ? 0.0 : (Double) xiaojiMap.get(endMonth + "月份");
                    xiaojiMap.put(endMonth + "月份", d + endMonthInvoice);
                }
                double sum = invoiceList.stream().filter(invoice -> getJi(invoice.getInvoiceDate().getMonth() + 1) == j).mapToDouble(Invoice::getInvoiceAmount).sum();
                leijiDep = leijiDep + sum;
                depMap.put(j + "季度", sum);
                Double d = xiaojiMap.get(j + "季度") == null ? 0.0 : (Double) xiaojiMap.get(j + "季度");
                xiaojiMap.put(j + "季度", d + sum);
            }
            xiaojiDep[0] = xiaojiDep[0] + leijiDep;
            depMap.put("累计开票", leijiDep);
            ja.add(depMap);
        });
        xiaojiMap.put("累计开票", xiaojiDep[0]);
        ja.add(xiaojiMap);
        ExcelUtil.downloadExcelFile("部门产值统计","期间：" + startDate + "--" + endDate, headerMap,ja, response, new int[]{});

    }


    private List<String> getJidu2(String startDate, String endDate) {
        List<String> jidu = new ArrayList<>();
        String[] start = startDate.split("-");
        String[] end = endDate.split("-");
        Integer startInt = Integer.valueOf(start[1]);
        Integer endInt = Integer.valueOf(end[1]);
        int j1 = getJi(startInt);
        int j2 = getJi(endInt);
        if (j2 > j1) {
            for (int i = j1; i <= j2 ; i++) {
                jidu.add(i + "季度");
            }
        } else {
            jidu.add(j1 + "季度");
        }

        jidu.add(jidu.size() - 1, endInt + "月份");
//        jidu.add("累计开票");
//        jidu.add("个人目标产值");
//        jidu.add("个人累计完成率");
//        jidu.add("小组合计");
//        jidu.add("小组目标产值");
//        jidu.add("参考累计完成率");
        return jidu;
    }

    private List<Integer> getJidu(String startDate, String endDate) {
        List<Integer> jidu = new ArrayList<>();
        String[] start = startDate.split("-");
        String[] end = endDate.split("-");
        Integer startInt = Integer.valueOf(start[1]);
        Integer endInt = Integer.valueOf(end[1]);
        int j1 = getJi(startInt);
        int j2 = getJi(endInt);
        if (j2 > j1) {
            for (int i = j1; i <= j2; i++) {
                jidu.add(i );
            }
        } else {
            jidu.add(j1);
        }
        return jidu;
    }

    private int getJi(int i) {
        if (i <= 3) {
            return 1;
        } else if (i <= 6) {
            return 2;
        } else if (i <= 9) {
            return 3;
        } else {
            return 4;
        }
    }


    private List<User> getGroupUser(List<Group> group) {
        List<List<User>> collect = group.stream().map(Group::getUsers).collect(Collectors.toList());
        List<User> groupUsers = new ArrayList<>();
        collect.forEach(groupUsers::addAll);
        return groupUsers;
    }

    private Group getOtherUser(List<User> allUser, List<Group> group) {
        List<String> collect = getGroupUser(group).stream().map(User::getName).collect(Collectors.toList());
        List<User> otherUsers = allUser.stream().filter(u -> !collect.equals(u.getName())).collect(Collectors.toList());
        Group g = new Group();
        g.setName("其他");
        g.setUsers(otherUsers);
        return g;
    }

    public static void main(String[] args) throws IOException {
        test();
    }

    public static void test() throws IOException {
        List<Map<String, Object>> colList = new ArrayList<Map<String, Object>>();
        Map<String, Object> value = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("jidu", "1季度");
        map.put("chanzhi", "t.cz_11");
        colList.add(map);
        map = new HashMap<>();
        map.put("jidu", "2季度");
        map.put("chanzhi", "t.cz_22");
        colList.add(map);
        value.put("colList", colList);

        List<Map<String, Object>> valList = new ArrayList<Map<String, Object>>();
        map = new HashMap<>();
        map.put("group", "1组");
        map.put("name", "景天");
        map.put("cz_11", "123");
        map.put("cz_22", "123");
        valList.add(map);

        map = new HashMap<>();
        map.put("group", "1组");
        map.put("name", "小张");
        map.put("cz_11", "456");
        map.put("cz_22", "456");
        valList.add(map);

        map = new HashMap<>();
        map.put("group", "2组");
        map.put("name", "小红");
        map.put("cz_11", "789");
        map.put("cz_22", "789");
        valList.add(map);

        value.put("valList", valList);
        value.put("date", "121212");


        TemplateExportParams params = new TemplateExportParams("E:\\ideaProjects\\invoice\\src\\main\\resources\\xxx.xlsx", true);
        params.setColForEach(true);
        //  params.setTemplateUrl("E:\\ideaProjects\\invoice\\src\\main\\resources\\xxx.xlsx");
        Workbook book = ExcelExportUtil.exportExcel(params, value);
        Sheet sheetAt = book.getSheetAt(0);
        //sheetAt.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
        //PoiMergeCellUtil.mergeCells(book.getSheetAt(0), 1, 0,1);
        FileOutputStream fos = new FileOutputStream("D:/excel/sdsdsds.xlsx");
        book.write(fos);
        fos.close();
//        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("content-Type", "application/vnd.ms-excel");
//        response.setHeader("Content-Disposition",
//                "attachment;filename=" + URLEncoder.encode("应收账款账龄分析明细.xlsx", "UTF-8"));
//        workbook.write(response.getOutputStream());
    }

}
