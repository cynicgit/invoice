package com.zhongyi.invoice.annontion;

import java.lang.annotation.*;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/13.
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface OperateLog {

    String value() default "";

}
