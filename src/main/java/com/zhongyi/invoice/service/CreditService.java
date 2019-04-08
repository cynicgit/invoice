package com.zhongyi.invoice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Credit;
import com.zhongyi.invoice.expection.BusinessException;
import com.zhongyi.invoice.mapper.CreditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/4/8.
 * @Description:
 */
@Service
public class CreditService {

    @Autowired
    private CreditMapper creditMapper;

    public void saveCredit(Credit credit){

        Credit byCreditLimit = creditMapper.findByCreditLimit(credit.getCreditLimit());
        if (byCreditLimit != null){
            throw new BusinessException("信用期限已存在");
        }
        creditMapper.insertSelective(credit);
    }


    public void updateCredit(Credit credit){
        Credit byCreditLimit = creditMapper.findByCreditLimit(credit.getCreditLimit());
        if (byCreditLimit != null){
            throw new BusinessException("信用期限已存在");
        }
        creditMapper.updateCredit(credit);
    }

    public void deleteCredit(Integer id){
        creditMapper.deleteCreditById(id);
    }

    public Credit findCreditById(Integer id){
       return creditMapper.getCreditById(id);
    }

    public Credit findCreditByCreditLimit(String creditLimit){
        return creditMapper.findByCreditLimit(creditLimit);
    }

    public PageInfo<Credit> getCreditPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Credit> taxRateAll = creditMapper.getCreditAll();
        return new PageInfo<>(taxRateAll);
    }

    public List<Credit> getCreditAll() {

        return creditMapper.getCreditAll();
    }
}
