package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.*;
import com.zhongyi.invoice.mapper.DepartmentMapper;
import com.zhongyi.invoice.mapper.InvoiceMapper;
import com.zhongyi.invoice.mapper.UserMapper;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceService {


    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserMapper userMapper;

    public Map<String, Object> importExcel(MultipartFile file) {
        List<Invoice> invoices = EasyPoiUtils.importExcel(file, 0, 1, Invoice.class);
        Map<String, Object> map = new HashMap<>();
        int zuofei = 0;
        int invoiceNumberRepeat = 0;
        List<Invoice> error = new ArrayList<>();
        for (int i = 0; i < invoices.size(); i++) {
            Invoice invoice = invoices.get(i);
            if (invoice.getTaskId() == null && "作废".equals(invoice.getInvoiceOffice())) {
                zuofei++;
                continue;
            }
            Invoice invoiceByTaskIdAndInvoiceNumber = invoiceMapper.getInvoiceByTaskIdAndInvoiceNumber(invoice.getTaskId(), invoice.getInvoiceNumber());
            if (invoiceByTaskIdAndInvoiceNumber != null) {
                invoiceNumberRepeat ++;
                continue;
            }
            String departmentName = invoice.getDepartmentName();
            String[] split = departmentName.split("-");
            Department depByName = departmentMapper.getDepByName(split[1]);
            if (depByName == null) {
                invoice.setErrorMsg("未找到部门");
                error.add(invoice);
                continue;
            }
            invoice.setDepId(depByName.getId());
            User userByName = userMapper.getUserByName(invoice.getContractUser());
            if (userByName == null) {
                invoice.setErrorMsg("未找到对应员工");
                error.add(invoice);
                continue;
            }
            invoice.setUserId(userByName.getId());


            // TODO 完整校验数据类型
            invoiceMapper.insert(invoice);
        }
        map.put("zuofei", zuofei);
        map.put("invoiceNumberRepeat", invoiceNumberRepeat);
        map.put("error", error);
        return map;
    }


    public List<InvoiceVO> noReceiveAmount(InvoiceVO invoice) {
//        if (invoice.getStartDate() == null || invoice.getEndDate() == null) {
//            throw new RuntimeException("参数错误");
//        }
        List<InvoiceVO> invoiceVOS = invoiceMapper.selectNoReceiveAmount(invoice);
        invoiceVOS.forEach(i -> {
            if (i.getReceivedAmount() < i.getInvoiceAmount()) {
                i.setNoReceivedAmount(i.getInvoiceAmount() - i.getReceivedAmount());
            }
        });
        return invoiceVOS;
    }

    public List<ReceivableStaticsInvoice> getInvoices(String startDate, String endDate) {
        List<ReceivableStaticsInvoice> invoices = invoiceMapper.getInvoices(startDate, endDate);
        invoices.forEach(i -> {
            if (i.getReceivedAmount() < i.getInvoiceAmount()) {
                i.setNoReceivedAmount(i.getInvoiceAmount() - i.getReceivedAmount());
            }
        });
        return invoices;
    }

    public List<InvoiceVO> receiptGatherStatistics(InvoiceVO invoiceVO) {
        List<InvoiceVO> invoiceVOS = invoiceMapper.selectReceiptGather(invoiceVO);
//        if ("2".equals(invoiceVO.getInvoiceType())) {
//            List<InvoiceVO> specialInvoices = invoiceVOS.stream().filter(invoiceVO1 -> {
//                return "0".equals(invoiceVO1.getInvoiceType());
//            }).collect(Collectors.toList());
//
//            List<InvoiceVO> commonInvoices = invoiceVOS.stream().filter(invoiceVO1 -> {
//                return "1".equals(invoiceVO1.getInvoiceType());
//            }).collect(Collectors.toList());
//        } else {
//
//        }

        return invoiceVOS;
    }

    public List<InvoiceVO> exportExcelReceiptDetail(InvoiceVO invoiceVO) {
        List<InvoiceVO> invoiceVOS = invoiceMapper.selectReceiptDetail(invoiceVO);
        return invoiceVOS;

    }

    public List<InvoiceVO> exportExcelPayedDetail(InvoiceVO invoiceVO) {
        List<InvoiceVO> invoiceVOS = invoiceMapper.selectPayedDetail(invoiceVO);
        return invoiceVOS;

    }

    public List<InvoiceVO> exportExcelPayedGather(InvoiceVO invoiceVO) {

       return invoiceMapper.selectPayedGather(invoiceVO);
    }

    public BasePageOutputDTO invoiceList(Integer pageSize, Integer pageNum,String startDate,String endDate,Integer type,String condition) {
        BasePageOutputDTO invoiceOutputDTO = new BasePageOutputDTO();
        PageHelper.startPage(pageNum, pageSize);
        List<InvoiceVO> list = invoiceMapper.listInvoices(startDate,endDate,condition);

        if (!StringUtils.isEmpty(condition)){
            if (type == 1){
                Map<String, List<InvoiceVO>> map = list.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getDepartmentName()));
                list = map.get(condition);
            }else if (type == 2){
                Map<String, List<InvoiceVO>> map = list.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getCreditLimit()));
                list = map.get(condition);
            }else if (type == 3){
                Map<String, List<InvoiceVO>> map = list.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getInvoiceType()));
                list = map.get(condition);
            }else if (type == 4){
                Map<String, List<InvoiceVO>> map = list.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getInvoiceOffice()));
                list = map.get(condition);
            }else if (type == 5){
                Map<String, List<InvoiceVO>> map = list.stream().collect(Collectors.groupingBy(invoiceVO2 -> invoiceVO2.getContractUser()));
                list = map.get(condition);
            }
        }


        PageInfo<InvoiceVO> pageInfo = new PageInfo<>(list);
        invoiceOutputDTO.setPage(pageInfo.getPages());
        invoiceOutputDTO.setList(pageInfo.getList());
        invoiceOutputDTO.setHasNext(pageInfo.isIsLastPage());
        invoiceOutputDTO.setTotal(pageInfo.getTotal());

        return invoiceOutputDTO;
    }

    public void saveInvoice(Invoice invoice) {

        invoiceMapper.insertSelective(invoice);
    }

    public void updateInvoice(Invoice invoice) {
        invoiceMapper.updateByPrimaryKeySelective(invoice);
    }

    public Invoice findById(Integer id) {

        return invoiceMapper.selectByPrimaryKey(id);
    }

    public void deleteInvoice(Integer id) {
        invoiceMapper.deleteByPrimaryKey(id);
    }

    public List<InvoiceVO> receiptGatherYearStatistics(InvoiceVO invoiceVO) {

       return invoiceMapper.listYearInvoices();
    }
}
