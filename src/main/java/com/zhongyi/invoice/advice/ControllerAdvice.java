package com.zhongyi.invoice.advice;

import com.zhongyi.invoice.entity.ZYResponse;
import com.zhongyi.invoice.expection.BusinessException;
import com.zhongyi.invoice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
public class ControllerAdvice {


    /**
     * 错误处理器
     *
     * @param e
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public ZYResponse  handleIOException(HttpServletRequest request, HttpServletResponse response, Model model, Exception e) throws IOException {
        log.info(e.toString());
        log.error("error", e);
        //请求类型,可以区分对待ajax和普通请求

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");



        if (e instanceof BusinessException) {
            BusinessException b = (BusinessException) e;
            return ZYResponse.notLogin().getResult() == b.getCode() ? ZYResponse.notLogin() : ZYResponse.error(b.getMessage());
        }


        return ZYResponse.error("服务器异常");

//        PrintWriter writer = response.getWriter();
//
//        writer.write(JsonUtils.objectToJsonNoNull(ZYResponse.error(e.getMessage())));
//        writer.flush();
//        writer.close();

    }

}
