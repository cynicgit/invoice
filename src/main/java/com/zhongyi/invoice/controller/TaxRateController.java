package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Project;
import com.zhongyi.invoice.entity.TaxRate;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.TaxRateService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/4/1.
 * @Description:
 */
@RestController
@RequestMapping("/taxrate")
public class TaxRateController {

    @Autowired
    private TaxRateService taxRateService;

    @PostMapping
    public ZYResponse saveTaxRate(@Valid TaxRate taxRate,BindingResult result){

        if(result.hasErrors()){
            return ZYResponse.error(result.getAllErrors().get(0));
        }
        taxRateService.saveTaxRate(taxRate);

        return ZYResponse.success();
    }

    @PutMapping
    public ZYResponse updateTaxRate(@Valid TaxRate taxRate,BindingResult result){
        if(result.hasErrors()){
            return ZYResponse.error(result.getAllErrors().get(0));
        }
        taxRateService.updateTaxRate(taxRate);
        return ZYResponse.success();
    }

    @DeleteMapping
    public ZYResponse deleteTaxRate(Integer id){
        taxRateService.deleteTaxRate(id);
        return ZYResponse.success();
    }

    @GetMapping
    public ZYResponse getTaxRateById(Integer id){
        TaxRate taxRate = taxRateService.getTaxRateById(id);
        return ZYResponse.success(taxRate);
    }

    @GetMapping("/all")
    public ZYResponse getTaxRateAll(){
        List<TaxRate> taxRateAll = taxRateService.getTaxRateAll();
        return ZYResponse.success(taxRateAll);
    }

    @GetMapping("/page")
    public ZYResponse getTaxRatePage(Integer pageNum,Integer pageSize){
        PageInfo<TaxRate> pageInfo  = taxRateService.getTaxRatePage(pageNum,pageSize);
        return ZYResponse.success(pageInfo);
    }

}
