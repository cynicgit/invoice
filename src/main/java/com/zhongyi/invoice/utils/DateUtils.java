package com.zhongyi.invoice.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/12.
 * @Description:
 */
public class DateUtils {

    public static String date2String(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        return  formatter.format(date);
    }

    public static void main(String[] args){

        Date date = new Date();
         System.out.println(date2String(date));
    }
}
