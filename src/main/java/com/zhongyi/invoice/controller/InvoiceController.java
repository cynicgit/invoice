package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.InvoiceOutputDTO;
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
    public ZYResponse invoiceList(Integer pageSize,Integer pageNum){
        InvoiceOutputDTO invoiceOutputDTO = invoiceService.invoiceList(pageSize, pageNum);
        return ZYResponse.success(invoiceOutputDTO);

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

    @GetMapping("/{id}")
    public ZYResponse findById(@PathParam("id") Integer id){
        Invoice invoice = invoiceService.findById(id);
        return ZYResponse.success(invoice);

    }

    @DeleteMapping("/{id}")
    public ZYResponse deleteInvoice(@PathParam("id") Integer id){
         invoiceService.deleteInvoice(id);
        return ZYResponse.success("删除成功");

    }
}
