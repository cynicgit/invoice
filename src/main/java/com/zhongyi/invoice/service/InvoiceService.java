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
            try {

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

                checkInvoice(invoice);
                invoiceMapper.insert(invoice);

            } catch (Exception e) {
                e.printStackTrace();
                invoice.setErrorMsg(e.getMessage());
                error.add(invoice);
            }
        }
        map.put("zuofei", zuofei);
        map.put("invoiceNumberRepeat", invoiceNumberRepeat);
        map.put("error", error);
        return map;
    }

    private void checkInvoice(Invoice invoice) throws Exception {
        if (StringUtils.isEmpty(invoice.getTaskId())) {
            throw new Exception("任务单号不能为空");
        }if (StringUtils.isEmpty(invoice.getContractNumber())) {
            throw new Exception("合同号不能为空");
        }if (StringUtils.isEmpty(invoice.getInvoiceDate())) {
            throw new Exception("任务单号不能为空");
        }if (invoice.getInvoiceDate() == null) {
            throw new Exception("开票日期不能为空格式不正确");
        }if (StringUtils.isEmpty(invoice.getCreditType())) {
            throw new Exception("信用类别不能为空");
        }if (StringUtils.isEmpty(invoice.getCreditLimit())) {
            throw new Exception("信用期限不能为空");
        }if (StringUtils.isEmpty(invoice.getInvoiceType())) {
            throw new Exception("发票类型不能为空");
        }if (StringUtils.isEmpty(invoice.getInvoiceNumber())) {
            throw new Exception("发票号不能为空");
        }if (StringUtils.isEmpty(invoice.getInvoiceOffice())) {
            throw new Exception("开票单位不能为空");
        }if (StringUtils.isEmpty(invoice.getDepartmentName())) {
            throw new Exception("所属部门不能为空");
        }if (StringUtils.isEmpty(invoice.getInvoiceProject())) {
            throw new Exception("项目不能为空");
        }if (invoice.getContractAmount() == null) {
            throw new Exception("合同金额不能为空");
        }if (invoice.getInvoiceAmount() == null) {
            throw new Exception("发票金额不能为空");
        }if (invoice.getNoTaxAmount() == null) {
            throw new Exception("不含税金额不能为空");
        }if (invoice.getContractUser() == null) {
            throw new Exception("项目负责人不能为空");
        }
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

    public BasePageOutputDTO invoiceList(Integer pageSize, Integer pageNum) {
        BasePageOutputDTO invoiceOutputDTO = new BasePageOutputDTO();
        PageHelper.startPage(pageNum, pageSize);
        List<InvoiceVO> list = invoiceMapper.listInvoices();
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
