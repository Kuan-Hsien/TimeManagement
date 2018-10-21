package com.realizeitstudio.deteclife.utils;

import android.support.constraint.ConstraintLayout;

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
            strOutput = hours + " hr";
        } else {
            strOutput = hours + " hr " + minutes + " min";
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
            strOutput = hours + " h";
        } else {
            strOutput = hours + " h " + minutes + " m";
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);

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
        long diffMin = diff / (1000 * 60);  // 先換成總共多少分鐘

        long hour = diffMin / 60;
        long min = diffMin % 60;

        return (hour + "hr " + min + "min") ;
    }


    // 計算傳入的時間 (下次提醒時間) 距離現在有多少毫秒。由於下次提醒時間可能是今天稍晚或是隔天此時，計算時需判斷是今天還是明天
    public static long getNextDailyNotifyMills(String strNotificationTime) {

        // (1) 先檢查傳入時間 (下次時間) 是今天還是明天
        Date curDate = new Date();
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);
        SimpleDateFormat simpleDateFormatHMS = new SimpleDateFormat(Constants.DB_FORMAT_HMS);

        String strCurTimeHms = simpleDateFormatHMS.format(curDate);    // 現在時間是 21:04:55
//        Logger.d(Constants.TAG, MSG + "getNextDailyNotifyMills -> yyyy/MM/dd: " + simpleDateFormatDate.format(curDate));
//        Logger.d(Constants.TAG, MSG + "getNextDailyNotifyMills -> yyyy/MM/dd HH:mm:ss: " + simpleDateFormatTime.format(curDate));
        Logger.d(Constants.TAG, MSG + "getNextDailyNotifyMills -> strCurHms: " + simpleDateFormatHMS.format(curDate));
        Logger.d(Constants.TAG, MSG + "getNextDailyNotifyMills -> strNotificationTime: " + strNotificationTime);

        String strNextDate = null;
        String strNextTime = null;

        // 找出隔天日期字串 (yyyymmdd)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1); // 先拿到隔天的日期


        // (1-1) 如果現在時間超過下次提醒時間，則下次提醒是明天 （e.g. 現在時間 09:08:32 ，提醒時間 08:00:00)
        if (strCurTimeHms.compareTo(strNotificationTime) >= 0) {

            Date nextDate = calendar.getTime();
            strNextDate = simpleDateFormatDate.format(nextDate); // 取 "明天" yyyy/MM/dd

        } else { // (1-2) 否則就是在今天稍晚提醒

            strNextDate = simpleDateFormatDate.format(curDate);  // 取 "今天" yyyy/MM/dd
        }

        // (2) 組出下一次通知時間字串 (yyyy/mm/dd + hh:mm:ss)
        strNextTime = strNextDate + " " + strNotificationTime; // "23:00:00" --> user 指定通知時間

        // (3) 算出距離現在多少毫秒
        // (3-1) 先把字串轉回 Date，失敗就直接明天此時提醒
        Date nextTime = null;


        try {
            Logger.d(Constants.TAG, MSG + "Next Time: " + strNextTime);

            nextTime = simpleDateFormatTime.parse(strNextTime);

        } catch (ParseException e) {

            Logger.d(Constants.TAG, MSG + "Exception while parsing nextTime");

            nextTime = calendar.getTime();
            e.printStackTrace();
        }

        // (3-2) 再用 Date.getTime() 得到毫秒數
        long diffMills = nextTime.getTime() - new Date().getTime();

        Logger.d(Constants.TAG, MSG + "Diff Mills: " + diffMills);
        Logger.d(Constants.TAG, MSG + "Diff Times: " + msToHourMin(diffMills) );

        return diffMills;
    }


    /**
     * 輸入日期，可以轉換成星期幾。(改用 calendar 轉，為了支援低版本不能用下方原本的 u pattern)
     *
     * @param dateString 日期字串
     * @return 星期幾
     * @throws ParseException 無法將字串轉換成 java.util.Date 類別
     *
     * 第一個SimpleDateFormat是用來將字串轉換成java.util.Date物件，如果輸入是java.util.Date則可以不用做字串轉Date的轉換。
     * 第二個SimpleDateFormat就是用來將日期轉換成星期幾，在new SimpleDateFormat("u") 的 u 稱為日期時間的 pattern，pattern 用來表示做日期格式化時分析時的樣式，而 u pattern 是用來表示一週的星期幾，並且以整數表示，1 ＝ 星期一 ... 7 ＝ 星期天。
     * 如果要直接轉換成「星期幾」而不是整數，則將 pattern 換成 E 即可。
     * 以上pattern的大小寫都要一樣!! 不一樣的大小寫在SimpleDateFormat會有不同的解讀，例如：大寫M表示月份，小寫m表示分鐘。
     */
    public static int date2Day( String dateString ) throws ParseException
    {
        SimpleDateFormat dateStringFormat = new SimpleDateFormat( Constants.DB_FORMAT_VER_NO );
        Date date = dateStringFormat.parse( dateString );

        return date2Day(date);
    }


    public static int date2Day( Date date )
    {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // date 轉換為 calendar

        // ******
        // (1) 得到今天星期幾
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);

        // (2) 一周第一天是否是星期天
        boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);

        // (3) 把星期幾的資訊換成我們預期的開始時間
        // calendar.setFirstDayOfWeek(Calendar.MONDAY); 只影響 WEEK_OF_YEAR，但不影響 DAY_OF_WEEK，所以要自己改
        // 舉例：星期一當第一天，回傳 1，星期五回傳 5，星期天回傳 7
        //      這在第一天是星期一的時候不用調整
        //      但第一天是星期天的時候要修正 (回傳值 -1)
        if (isFirstSunday) {
            weekDay = weekDay - 1;

            if (weekDay == 0) {
                weekDay = 7;
            }
        }

//        Logger.d(Constants.TAG, MSG + "date2Day:Calendar.DAY_OF_WEEK => " + calendar.get(Calendar.DAY_OF_WEEK));
        Logger.d(Constants.TAG, MSG + "date2Day:weekDay => " + weekDay);
//        Logger.d(Constants.TAG, MSG + "date2Day:pattern = 'u' => " + new SimpleDateFormat( "u" ).format( date ));
//        Logger.d(Constants.TAG, MSG + "date2Day:pattern = 'E' => " + new SimpleDateFormat( "E" ).format( date ));
//        Logger.d(Constants.TAG, MSG + "date2Day:pattern = 'F' => " + new SimpleDateFormat( "F" ).format( date ));   // Today is the second Wednesday in the current month.


        return weekDay;
    }


}
