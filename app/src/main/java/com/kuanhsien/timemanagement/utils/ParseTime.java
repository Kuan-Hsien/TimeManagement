package com.kuanhsien.timemanagement.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ParseTime {
    private static final String MSG = "ParseTime: ";

    // 輸入 min 數，回傳 3hr 15min 字串
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

    // 輸入 min 數，回傳 3h 15m 字串
    public static String intToHrM(int intInput) {

        int hours = intInput / 60;
        int minutes = intInput % 60;

        String strOutput;

        if (hours == 0) {
            strOutput = minutes + " m";
        } else if (minutes == 0) {
            strOutput = hours + "h";
        } else {
            strOutput = hours + "h " + minutes + " m";
        }

        return strOutput;
    }

    // 輸入 ms，回傳所代表的 yyyy/MM/dd hh:mm:ss 字串
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

    // 輸入 ms，回傳 3hr 15min 字串
    public static String msToHourMin(long ms) {

        return intToHourMin((int) ms / (1000 * 60));
    }

    // 輸入 ms，回傳 3h 15m 字串
    public static String msToHrM(long ms) {

        return intToHrM((int) ms / (1000 * 60));
    }

    // 輸入開始和結束的 ms 數，回傳間隔 3hr 15min 字串
    public static String msToHourMinDiff(long begin, long end) {

        long diff = end - begin;
        long hour = diff / (1000 * 60 * 60);
        long min = diff / (1000 * 60);

        return (hour + "hr " + min + "min") ;
    }


    public static long getNextDailyNotifyMills(String strNotificationTime) {

        // (1) 找出隔天日期字串 (yyyymmdd)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1); // 先拿到隔天的日期
        Date nextDate = calendar.getTime();

        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyyMMdd");
        String strNextDate = simpleDateFormatDate.format(nextDate);

        // (2) 組出下一次通知時間 (yyyymmdd hh:mm:ss)
        String strNextTime = strNextDate + " " + strNotificationTime; // "23:00:00" --> user 指定通知時間
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
        Date nextTime = null;

        try {
            Logger.d(Constants.TAG, MSG + "Next Time: " + strNextTime);

            nextTime = simpleDateFormatTime.parse(strNextTime);

        } catch (ParseException e) {

            Logger.d(Constants.TAG, MSG + "Exception while parsing nextTime");

            nextTime = calendar.getTime();
            e.printStackTrace();
        }

        // (3) 算出距離現在多少毫秒
        long diffMills = nextTime.getTime() - new Date().getTime();

        Logger.d(Constants.TAG, MSG + "Diff Mills: " + diffMills);
        Logger.d(Constants.TAG, MSG + "Diff Times: " + msToHourMin(diffMills) );

        return diffMills;
    }

}
