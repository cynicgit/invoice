package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.TaxRate;
import com.zhongyi.invoice.mapper.TaxRateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/4/1.
 * @Description:
 */
@Service
public class TaxRateService {


    @Autowired
    private TaxRateMapper taxRateMapper;

    public void saveTaxRate(TaxRate taxRate){
       taxRateMapper.insertSelective(taxRate);
    }

    public void updateTaxRate(TaxRate taxRate){
        taxRateMapper.updateTaxRate(taxRate);
    }

    public void deleteTaxRate(Integer id){
        taxRateMapper.deleteTaxRateById(id);
    }

    public TaxRate getTaxRateById(Integer id){
        return taxRateMapper.getTaxRateById(id);
    }

    public List<TaxRate> getTaxRateAll(){
        return taxRateMapper.getTaxRateAll();
    }

    public PageInfo<TaxRate> getTaxRatePage(Integer pageNum, Integer pageSize) {


        PageHelper.startPage(pageNum, pageSize);
        List<TaxRate> taxRateAll = taxRateMapper.getTaxRateAll();
        return new PageInfo<>(taxRateAll);
    }
}
