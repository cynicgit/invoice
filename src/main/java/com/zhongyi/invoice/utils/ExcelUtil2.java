package com.zhongyi.invoice.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtil2 {
    public static String NO_DEFINE = "no_define";//未定义的字段
    public static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";//默认日期格式
    public static int DEFAULT_COLOUMN_WIDTH = 17;

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     *
     * @param title       标题行
     * @param headMap     属性-列头
     * @param jsonArray   数据集
     * @param datePattern 日期格式，传null值则默认 年月日
     * @param colWidth    列宽 默认 至少17个字节
     * @param out         输出流
     */
    public static void exportExcelX(String title, Map<String, String> headMap, JSONArray jsonArray, String datePattern, int colWidth, OutputStream out) {
        if (datePattern == null) datePattern = DEFAULT_DATE_PATTERN;
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);//缓存
        workbook.setCompressTempFiles(true);
        //表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBold(true);

        titleStyle.setFont(titleFont);
        // 列头样式
        CellStyle headerStyle = workbook.createCellStyle();
        // headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        //  cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font cellFont = workbook.createFont();
        //cellFont.setBold(true);
        cellStyle.setFont(cellFont);
        // 生成一个(带标题)表格
        SXSSFSheet sheet = workbook.createSheet();
        //设置列宽
        int minBytes = colWidth < DEFAULT_COLOUMN_WIDTH ? DEFAULT_COLOUMN_WIDTH : colWidth;//至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter
                .hasNext(); ) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = headMap.get(fieldName);

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : jsonArray) {
            if (rowIndex == 65535 || rowIndex == 0) {
                if (rowIndex != 0) sheet = workbook.createSheet();//如果数据超过了，则在第二页显示

//                SXSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
//                titleRow.createCell(0).setCellValue(title);
//                titleRow.getCell(0).setCellStyle(titleStyle);
//                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headMap.size() - 1));

                SXSSFRow headerRow = sheet.createRow(0); //列头 rowIndex =1
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);

                }
                rowIndex = 1;//数据内容从 rowIndex=2开始
            }
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            SXSSFRow dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                SXSSFCell newCell = dataRow.createCell(i);

                Object o = jo.get(properties[i]);
                String cellValue = "";
                if (o == null) {
                    cellValue = "";
                } else if (o instanceof Date) {
                    cellValue = new SimpleDateFormat(datePattern).format(o);
                } else if (o instanceof Float || o instanceof Double) {
                    cellValue = new BigDecimal(o.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                } else if (o instanceof JSONObject) {
                    JSONObject j = (JSONObject) o;
                    long date = (long) ((JSONObject) j.get("loginInfo1")).get("date") * 1000;
                    cellValue = new SimpleDateFormat(datePattern).format(date);
                } else {
                    if (o.toString().matches("^\\d{13}$")) {
                        try {
                            cellValue = new SimpleDateFormat(datePattern).format(o);

                        } catch (Exception e) {
                            cellValue = o.toString();
                        }
                    } else if (o.toString().matches("^\\d{10}$")) {
                        try {
                            cellValue = new SimpleDateFormat(datePattern).format(new Date(Integer.parseInt(o.toString()) * 1000L));
                        } catch (Exception e) {
                            cellValue = o.toString();
                        }
                    } else {
                        cellValue = o.toString();
                    }
                }

                newCell.setCellValue(cellValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
        try {
            workbook.write(out);
            workbook.close();
            workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportExcelX2(String title, Map<String, String> headMap, JSONArray jsonArray, String datePattern, double hejiValue, OutputStream out) {
        if (datePattern == null) datePattern = DEFAULT_DATE_PATTERN;
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);//缓存
        workbook.setCompressTempFiles(true);
        //表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBold(true);

        titleStyle.setFont(titleFont);
        // 列头样式
        CellStyle headerStyle = workbook.createCellStyle();
        // headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        //  cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font cellFont = workbook.createFont();
        //cellFont.setBold(true);
        cellStyle.setFont(cellFont);
        // 生成一个(带标题)表格
        SXSSFSheet sheet = workbook.createSheet();
        //设置列宽
        int minBytes =  DEFAULT_COLOUMN_WIDTH ;//至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter
                .hasNext(); ) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = headMap.get(fieldName);

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : jsonArray) {
            if (rowIndex == 65535 || rowIndex == 0) {
                if (rowIndex != 0) sheet = workbook.createSheet();//如果数据超过了，则在第二页显示

                SXSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
                SXSSFRow titleRow2 = sheet.createRow(1);//表头 rowIndex=0
                titleRow.createCell(0).setCellValue(title);
                titleRow.getCell(0).setCellStyle(titleStyle);
                int zhanglingSize = headMap.size() - 8 - 1;
                sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, zhanglingSize));
                titleRow.createCell(zhanglingSize + 1).setCellValue("总计: " + new BigDecimal(hejiValue).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                SXSSFCell hejiValueCell = titleRow.createCell(zhanglingSize + 2);
                hejiValueCell.setCellValue("万元");

                SXSSFCell zhanglingCell = titleRow2.createCell(zhanglingSize + 1);
                zhanglingCell.setCellValue("账龄分析");
                zhanglingCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, zhanglingSize + 1, headMap.size() - 1));



                SXSSFRow headerRow = sheet.createRow(2); //列头 rowIndex =1
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);

                }
                rowIndex = 3;//数据内容从 rowIndex=2开始
            }
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            SXSSFRow dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                SXSSFCell newCell = dataRow.createCell(i);

                Object o = jo.get(properties[i]);
                String cellValue = "";
                if (o == null) {
                    cellValue = "";
                } else if (o instanceof Date) {
                    cellValue = new SimpleDateFormat(datePattern).format(o);
                } else if (o instanceof Float || o instanceof Double) {
                    cellValue = new BigDecimal(o.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                } else if (o instanceof JSONObject) {
                    JSONObject j = (JSONObject) o;
                    long date = (long) ((JSONObject) j.get("loginInfo1")).get("date") * 1000;
                    cellValue = new SimpleDateFormat(datePattern).format(date);
                } else {
                    if (o.toString().matches("^\\d{13}$")) {
                        try {
                            cellValue = new SimpleDateFormat(datePattern).format(o);

                        } catch (Exception e) {
                            cellValue = o.toString();
                        }
                    } else if (o.toString().matches("^\\d{10}$")) {
                        try {
                            cellValue = new SimpleDateFormat(datePattern).format(new Date(Integer.parseInt(o.toString()) * 1000L));
                        } catch (Exception e) {
                            cellValue = o.toString();
                        }
                    } else {
                        cellValue = o.toString();
                    }
                }

                newCell.setCellValue(cellValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
        try {
            workbook.write(out);
            workbook.close();
            workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportExcelX(String title, Map<String, String> headMap, List list, OutputStream out) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(list);
        exportExcelX(title, headMap, jsonArray, null, 0, out);
    }

    //Web 导出excel
    public static void downloadExcelFile(String title, Map<String, String> headMap, JSONArray ja, HttpServletResponse response) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ExcelUtil2.exportExcelX(title, headMap, ja, null, 0, os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((title + ".xlsx").getBytes(), "iso-8859-1"));
            response.setContentLength(content.length);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);

            }
            bis.close();
            bos.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadExcelFile2(String title, Map<String, String> headMap, JSONArray ja, double hejiValue, HttpServletResponse response) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ExcelUtil2.exportExcelX2(title, headMap, ja, null, hejiValue, os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((title + ".xlsx").getBytes(), "iso-8859-1"));
            response.setContentLength(content.length);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);

            }
            bis.close();
            bos.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadExcelFile(String title, Map<String, String> headMap, List list, HttpServletResponse response) {
        JSONArray ja = new JSONArray();
        ja.addAll(list);
        downloadExcelFile(title, headMap, ja, response);
    }

    public static Map<String,String> getHeaderMap(){
        Map<String,String> headMap = new LinkedHashMap<>();
        headMap.put("taskId", "任务单号");
        headMap.put("contractNumber", "合同号");
        headMap.put("invoiceDate", "开票日期");
        headMap.put("creditType", "信用类别");
        headMap.put("creditLimit", "信用期限");
        headMap.put("invoiceNumber", "发票号");
        headMap.put("invoiceOffice", "开票单位");
        headMap.put("departmentName", "所属部门");
        headMap.put("invoiceProject", "项目");
        headMap.put("contractAmount", "合同金额");
        headMap.put("invoiceAmount", "发票金额");
        headMap.put("noTaxAmount", "不含税金额");
        headMap.put("contractUser", "项目负责人");
        headMap.put("invoiceSignatory", "签收人");
        headMap.put("reportNumber", "报告号");
        headMap.put("reportDate", "报告日期");
        headMap.put("receivedDate", "回款日期");
        headMap.put("receivedAmount", "回款金额");
        headMap.put("noReceivedAmount", "未到账");
        headMap.put("badAmount", "坏账");
        headMap.put("descprition", "备注");
        return headMap;
    }

    public static Map<String,String> getZhanglingHeaderMap(){
        Map<String,String> headMap = new LinkedHashMap<>();
        headMap.put("taskId", "任务单号");
        headMap.put("contractNumber", "合同号");
        headMap.put("invoiceDate", "开票日期");
        headMap.put("creditType", "信用类别");
        headMap.put("creditLimit", "信用期限");
        headMap.put("invoiceNumber", "发票号");
        headMap.put("invoiceOffice", "开票单位");
        headMap.put("departmentName", "所属部门");
        headMap.put("invoiceProject", "项目");
        headMap.put("contractAmount", "合同金额");
        headMap.put("invoiceAmount", "发票金额");
        headMap.put("noTaxAmount", "不含税金额");
        headMap.put("contractUser", "项目负责人");
        headMap.put("receivedDate", "回款日期");
        headMap.put("receivedAmount", "回款金额");
        headMap.put("noReceivedAmount", "未到账");
        headMap.put("limitAmount0", "小于3月");
        headMap.put("limitAmount1", "3-6月");
        headMap.put("limitAmount2", "6-9月");
        headMap.put("limitAmount3", "9-12月");
        headMap.put("limitAmount4", "1-2年");
        headMap.put("limitAmount5", "2-3年");
        headMap.put("limitAmount6", "大于3年");
        headMap.put("badAmount", "坏账");
        return headMap;
    }

    public static void main(String[] args) throws IOException {


    }
}