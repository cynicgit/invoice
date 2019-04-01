package com.zhongyi.invoice.mapper;

import com.zhongyi.invoice.entity.TaxRate;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/4/1.
 * @Description:
 */
public interface TaxRateMapper {


    void insertSelective(TaxRate taxRate);

    void deleteTaxRateById(Integer id);

    void updateTaxRate(TaxRate taxRate);

    List<TaxRate> getTaxRateAll();

    TaxRate getTaxRateById(Integer id);
}
