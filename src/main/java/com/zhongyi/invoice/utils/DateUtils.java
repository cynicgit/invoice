package com.zhongyi.invoice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: huguanghui
 * Created by huguanghui on 2019/3/12.
 * @Description:
 */
public class DateUtils {

    public static String date2String(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        return formatter.format(date);
    }


    public static Date string2Date(String strDate) {


        //注意：SimpleDateFormat构造函数的样式与strDate的样式必须相符
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        //必须捕获异常
        try {
            Date date = simpleDateFormat.parse(strDate);
            System.out.println(date);
            return date;
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return null;

    }

    public static void main(String[] args) {

//        Map<String,String> map = new HashMap<>();
//       String string = "2018-01";
//        map.put(string,"1");
//        Date date1 = string2Date(string);
//        String string2 = "2018-02";
//        map.put(string2,"2");
//        Date date2 = string2Date(string2);
//        int i = date1.compareTo(date2);
//         System.out.println(i);
//        List<String> collect = map.keySet().stream().sorted(new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                Date date1 = string2Date(o1);
//                Date date2 = string2Date(o2);
//                return date1.compareTo(date2);
//            }
//        }).collect(Collectors.toList());
//
//        collect.forEach(System.out::println);



    }
}
