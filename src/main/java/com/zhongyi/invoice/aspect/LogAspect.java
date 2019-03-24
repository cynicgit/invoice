package com.zhongyi.invoice.aspect;

import com.zhongyi.invoice.annontion.OperateLog;
import com.zhongyi.invoice.entity.SystemLog;
import com.zhongyi.invoice.entity.User;
import com.zhongyi.invoice.mapper.SystemLogMapper;
import com.zhongyi.invoice.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/13.
 * @Description: 操作日志切面
 */
@Aspect
@Component
@Slf4j
public class LogAspect {


    @Autowired
    private SystemLogService systemLogService;


    @Around(value = "@annotation(operateLog)")
    public Object operateLog(ProceedingJoinPoint joinPoint, OperateLog operateLog) throws Throwable {

        // 没有参数
//        if (joinPoint.getArgs() == null) {
//            return;
//        }

        SystemLog logPO = new SystemLog();
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        assert sra != null;
        HttpServletRequest request = sra.getRequest();
        String url = request.getRequestURL().toString();
        logPO.setRequestUrl(url);

        String name = joinPoint.getSignature().getName();
        logPO.setMethod(name);
        Object user = request.getSession().getAttribute("user");
        if (user != null) {
            User u = (User) user;
            logPO.setUsername(u.getName());
        }


        logPO.setDescription(operateLog.value());
        Object[] args = joinPoint.getArgs();
        logPO.setParams(Arrays.asList(args).toString());
        try {
            logPO.setException("无");
            return joinPoint.proceed();
        } catch (Throwable e) {
            logPO.setException(e.toString());
            throw e;
        } finally {
            systemLogService.addLog(logPO);
        }

    }

}