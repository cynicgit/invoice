package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.InvoiceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

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
}