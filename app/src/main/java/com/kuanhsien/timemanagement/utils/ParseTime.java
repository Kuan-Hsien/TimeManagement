package com.kuanhsien.timemanagement.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseTime {

    public static String intToHourMin(int intInput) {

        int hours = intInput / 60;
        int minutes = intInput % 60;

        String strOutput;

        if (hours == 0) {
            strOutput = minutes + " min";
        } else if (minutes == 0) {
            strOutput = hours + "hr";
        } else {
            strOutput = hours + "hr " + minutes + " min";
        }

        return strOutput;
    }


    public static String msToStr(long ms) {

        // 取得 1970 開始的毫秒數，可以用 input 的毫秒數算出所代表的時間
        // 1970 時毫秒數 1317427200

        // (Method-1)
//        Date begin = new Date(1317427200);

        // (Method-2)
//        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        ParsePosition pos = new ParsePosition(0);
//        Date d1 = (Date) sd.parse(createtime, pos);


        // (Method-3)
        Date date = new Date(ms);

        // 定義時間格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        // 透過SimpleDateFormat的format方法將 Date 轉為字串
        String strTime = simpleDateFormat.format(date);

        return strTime;
    }


    public static String msToStrDiff(long begin, long end) {

        long diff = end - begin;
        long hour = diff / (1000 * 60 * 60);
        long min = diff / (1000 * 60);

        return (hour + "hr " + min + "min") ;
    }
}
