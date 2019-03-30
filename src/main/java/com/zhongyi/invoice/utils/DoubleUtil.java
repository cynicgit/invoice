package com.zhongyi.invoice.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/28.
 * @Description:
 */
public class DoubleUtil {

    //保留2位小数
//    public static Double getExactDouble(Double num){
//
//        BigDecimal b = new BigDecimal(num);
//        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//    }

    public static String getNum(Object num) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }
}
