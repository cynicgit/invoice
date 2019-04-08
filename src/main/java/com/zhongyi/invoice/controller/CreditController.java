package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Credit;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/4/8.
 * @Description:
 */
@RequestMapping("/credit")
@RestController
public class CreditController {


    @Autowired
    private CreditService creditService;

    @PostMapping
    public ZYResponse saveCredit(Credit credit){

        creditService.saveCredit(credit);
        return ZYResponse.success();

    }

    @PutMapping
    public ZYResponse updateCredit(Credit credit){
        creditService.updateCredit(credit);
        return ZYResponse.success();
    }


    @DeleteMapping
    public ZYResponse deleteCredit(Integer id){
        creditService.deleteCredit(id);
        return ZYResponse.success();
    }

    @GetMapping
    public ZYResponse findCreditById(Integer id){
        Credit credit = creditService.findCreditById(id);
        return ZYResponse.success(credit);
    }

    @GetMapping("/page")
    public ZYResponse getCreditPage(Integer pageNum,Integer pageSize){
        PageInfo<Credit> creditPage = creditService.getCreditPage(pageNum, pageSize);
        return ZYResponse.success(creditPage);
    }


    @GetMapping("/all")
    public ZYResponse getCreditAll(){
        List<Credit> creditPage = creditService.getCreditAll();
        return ZYResponse.success(creditPage);
    }

}
