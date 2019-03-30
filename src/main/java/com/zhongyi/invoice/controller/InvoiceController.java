package com.zhongyi.invoice.controller;

import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.BasePageOutputDTO;
import com.zhongyi.invoice.entity.Invoice;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/12.
 * @Description:
 */
@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Value("${invoice.login:true}")
    private boolean isLogin;
    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/page")
    public ZYResponse invoiceList(@RequestParam(defaultValue = "20") Integer pageSize,
                                  @RequestParam(defaultValue = "1")Integer pageNum,
                                  Integer type, String startDate, String endDate, String condition, HttpSession session){
        User user = (User) session.getAttribute("user");
        String name = "";
        if ( isLogin && user.getType() == 2) {
            name = user.getName();
        }
        BasePageOutputDTO basePageOutputDTO = invoiceService.invoiceList(pageSize, pageNum,startDate,endDate,type,condition, name);
        return ZYResponse.success(basePageOutputDTO);

    }

    @PostMapping()
    @OperateLog("发票添加")
    public ZYResponse saveInvoice(Invoice invoice){
         invoiceService.saveInvoice(invoice);
        return ZYResponse.success("添加成功");

    }

    @PutMapping()
    @OperateLog("发票更新")
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
    @OperateLog("发票删除")
    public ZYResponse deleteInvoice(@PathVariable Integer id){
         invoiceService.deleteInvoice(id);
        return ZYResponse.success("删除成功");

    }
}
