package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.InvoiceVO;
import com.zhongyi.invoice.entity.ReceivableStaticsInvoice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Mapper
@Component
public interface InvoiceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Invoice record);

    int insertSelective(Invoice record);

    Invoice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Invoice record);

    int updateByPrimaryKey(Invoice record);

    Invoice getInvoiceByTaskIdAndInvoiceNumber(@Param("taskId") String taskId, @Param("invoiceNumber") String invoiceNumber);

    List<InvoiceVO> selectNoReceiveAmount(InvoiceVO invoice);

    List<InvoiceVO> selectReceiptGather(InvoiceVO invoiceVO);

    List<InvoiceVO> selectReceiptDetail(InvoiceVO invoiceVO);

    List<InvoiceVO> selectPayedDetail(InvoiceVO invoiceVO);

    List<InvoiceVO> selectPayedGather(InvoiceVO invoiceVO);

    List<ReceivableStaticsInvoice> getInvoices(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<InvoiceVO> listInvoices(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("condition") String condition, @Param("name")String name);
    List<InvoiceVO> listYearInvoices(@Param("startDate")String startDate,@Param("endDate")String endDate);

    Integer findByTaskId(String taskId);

    Integer findByInvoiceNumber(String invoiceNumber);

    List<InvoiceVO> getInvoiceByUserName(@Param("name") String name);
}