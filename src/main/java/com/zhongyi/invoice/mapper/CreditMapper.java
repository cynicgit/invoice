package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.Credit;
import com.zhongyi.invoice.entity.TaxRate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/4/8.
 * @Description:
 */
public interface CreditMapper {
    void insertSelective(Credit credit);

    void deleteCreditById(Integer id);

    void updateCredit(Credit credit);

    List<Credit> getCreditAll();

    Credit getCreditById(Integer id);

    Credit findByCreditLimit(@Param("creditLimit") String creditLimit);
}
