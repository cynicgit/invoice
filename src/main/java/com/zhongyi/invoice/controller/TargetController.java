package com.zhongyi.invoice.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.Target;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.service.TargetService;
import com.zhongyi.invoice.style.MyExcelExportStylerDefaultImpl;
import com.zhongyi.invoice.utils.EasyPoiUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/system/target")
public class TargetController {


    @Autowired
    private TargetService targetService;

    @PostMapping
    @OperateLog("年度目标添加")
    public ZYResponse addTarget(Target target) throws Exception {
        targetService.addTarget(target);
        return ZYResponse.success();
    }

    @PutMapping
    @OperateLog("年度目标更新")
    public ZYResponse updateTarget(Target target) throws Exception {
        targetService.updateTarget(target);
        return ZYResponse.success();
    }

    @GetMapping
    public ZYResponse getTarget(Integer pageNum, Integer pageSize, Target target) throws Exception {
        PageInfo<Target> pageInfo = targetService.getTarget(pageNum, pageSize, target);
        return ZYResponse.success(pageInfo);
    }

    @GetMapping("/{id}")
    public ZYResponse getTarget(@PathVariable Integer id) throws Exception {
        Target pageInfo = targetService.getTargetById(id);
        return ZYResponse.success(pageInfo);
    }

    @DeleteMapping("/{id}")
    @OperateLog("年度目标删除")
    public ZYResponse deleteTarget(@PathVariable Integer id) throws Exception {
        targetService.deleteTarget(id);
        return ZYResponse.success();
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response, String year) throws IOException {
        List<Target> targets = targetService.getAllGroupTarget(year);
        List<Target> targets2 = targetService.getAllUserTarget(year);
        targets.addAll(targets2);
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        exportParams.setStyle(MyExcelExportStylerDefaultImpl.class);
        EasyPoiUtils.defaultExport(targets, Target.class,  year+"年度目标.xlsx", response, exportParams);
    }

}
