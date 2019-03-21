package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.BasePageOutputDTO;
import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/12.
 * @Description:
 */
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/page")
    public ZYResponse invoiceList(@RequestParam(defaultValue = "20") Integer pageSize,
                                  @RequestParam(defaultValue = "1")Integer pageNum,
                                  Integer type,String startDate,String endDate,String condition){
        BasePageOutputDTO basePageOutputDTO = invoiceService.invoiceList(pageSize, pageNum,startDate,endDate,type,condition);
        return ZYResponse.success(basePageOutputDTO);

    }

    @PostMapping()
    public ZYResponse saveInvoice(Invoice invoice){
         invoiceService.saveInvoice(invoice);
        return ZYResponse.success("添加成功");

    }

    @PutMapping()
    public ZYResponse updateInvoice(Invoice invoice){
        invoiceService.updateInvoice(invoice);
        return ZYResponse.success("修改成功");

    }

    @GetMapping()
    public ZYResponse findById( Integer id){
        Invoice invoice = invoiceService.findById(id);
        return ZYResponse.success(invoice);

    }

    @DeleteMapping("/{id}")
    public ZYResponse deleteInvoice(@PathParam("id") Integer id){
         invoiceService.deleteInvoice(id);
        return ZYResponse.success("删除成功");

    }
}
