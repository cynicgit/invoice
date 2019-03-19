package com.zhongyi.invoice.entity;

import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Data
@Builder
public class DayCompare {
    private int year;
    private int month;
    private int day;

    public static DayCompare dayComparePrecise(Date fromDate, Date toDate) {
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar to = Calendar.getInstance();
        to.setTime(toDate);

        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int fromDay = from.get(Calendar.DAY_OF_MONTH);

        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int toDay = to.get(Calendar.DAY_OF_MONTH);
        int year = toYear - fromYear;
        int month = toMonth - fromMonth;
        int day = toDay - fromDay;
        return DayCompare.builder().day(day).month(month).year(year).build();
    }

    public static DayCompare dayCompare(Date fromDate, Date toDate) {
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar to = Calendar.getInstance();
        to.setTime(toDate);
        //只要年月
        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);

        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);

        int year = toYear - fromYear;
        int month = toYear * 12 + toMonth - (fromYear * 12 + fromMonth);
        int day = (int) ((to.getTimeInMillis() - from.getTimeInMillis()) / (24 * 3600 * 1000));
        return DayCompare.builder().day(day).month(month).year(year).build();
    }

    public static String yearCompare(Date fromDate, Date toDate) {
        DayCompare result = dayComparePrecise(fromDate, toDate);
        double month = result.getMonth();
        double year = result.getYear();
        //返回2位小数，并且四舍五入
        DecimalFormat df = new DecimalFormat("######0.0");
        return df.format(year + month / 12);
    }

    public static int getMonthBetween(Date fromDate, Date toDate) {
        DateTime startTime = new DateTime(fromDate);
        DateTime endTime = new DateTime(toDate);
        int months = Months.monthsBetween(startTime, endTime).getMonths();
        return months;
    }

}