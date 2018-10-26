package com.realizeitstudio.deteclife;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.object.CategoryDefineTable;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.object.TimePlanningTable;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.service.JobSchedulerServiceDailyDataVersionGeneration;
import com.realizeitstudio.deteclife.service.JobSchedulerServiceDailySummary;
import com.realizeitstudio.deteclife.service.MainService;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import io.fabric.sdk.android.Fabric;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Ken on 2018/9/24.
 */
public class TimeManagementApplication extends Application {
    private static final String MSG = "TimeManagementApplication: ";

    private static TimeManagementApplication mContext;
    private static SharedPreferences mSharePreferences;
    private static boolean mIsFirstLogin;



//    public static DbFavoriteArticle dbFavoriteArticle;

//    public static int appModeValue;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = this;

        Logger.d(Constants.TAG, MSG + getSharedPreferences(Constants.FILENAME_USER_DATA, Context.MODE_PRIVATE)
                .getString(Constants.IS_FIRST_FLAG, "TRUE"));

        // [IS_FIRST_FLAG = true] if first login
        if ("TRUE".equals(
                getSharedPreferences(Constants.FILENAME_USER_DATA, Context.MODE_PRIVATE)
                        .getString(Constants.IS_FIRST_FLAG, "TRUE"))) {


            Logger.d(Constants.TAG, MSG + "TRUE");

            setFirstLogin(true);
            firstLogin();

        } else {

            Logger.d(Constants.TAG, MSG + "FALSE");
            setFirstLogin(false);
        }

        init();

//        dbFavoriteArticle = new DbFavoriteArticle(applicationContext);
//
//        // set APP mode from file in external storage
//        //
//        // read control file from external storage
//        File fileControl = new File(Environment.getExternalStorageDirectory().getPath() + Constants.FILE_PATH_EXTERNAL_STORAGE , Constants.FILENAME_APP_MODE);
//        String strAppMode = null;
//
//        // check if exist External Control File
//        if (fileControl.exists()) {
//            strAppMode = new ExternalStorageHelper().readFile(Constants.FILENAME_APP_MODE);
//        }
//        setAppMode(strAppMode);
    }

    public static TimeManagementApplication getAppContext() {
        return mContext;
    }

//    public static DbFavoriteArticle getDatabase() {
//        return dbFavoriteArticle;
//    }

//    public void setAppMode(String appMode) {
//
//        if (Constants.APP_MODE_DEMO.equals(appMode)) {
//            this.appModeValue = Constants.APP_MODE_VALUE_DEMO;
//
//        } else if (Constants.APP_MODE_DEV.equals(appMode)) {
//            this.appModeValue = Constants.APP_MODE_VALUE_DEV;
//
//        } else {
//            this.appModeValue = Constants.APP_MODE_VALUE_RLS;
//        }
//    }
//
//    public static int getAppMode() {
//        return appModeValue;
//    }


    // 第一個參數是檔名，第二個參數是你要找的Resource class。可取出 getResId("filename", R.drawable.class)
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Drawable getIconResourceDrawable(String strIcon) {
        return TimeManagementApplication.getAppContext().getDrawable(getResId(strIcon, R.drawable.class));
    }

//    public static Drawable getIconResourceDrawable(String strIcon) {
//
//        if (strIcon.equals(Constants.APP_ICON_SMALL)) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.deteclife_icon_line);
//        } else if (strIcon.equals(Constants.APP_ICON_BIG)) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.deteclife_icon_fill);
//
//        } else if (strIcon.equals("icon_sleep")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_sleep);
//        } else if (strIcon.equals("icon_bike")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_bike);
//        } else if (strIcon.equals("icon_book")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_book);
//        } else if (strIcon.equals("icon_car")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_car);
//        } else if (strIcon.equals("icon_computer")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_computer);
//        } else if (strIcon.equals("icon_drunk")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_drunk);
//        } else if (strIcon.equals("icon_friend")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_friend);
//        } else if (strIcon.equals("icon_food")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_food);
//        } else if (strIcon.equals("icon_home")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_home);
//        } else if (strIcon.equals("icon_lover")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_lover);
//        } else if (strIcon.equals("icon_music")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_music);
//        } else if (strIcon.equals("icon_paw")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_paw);
//        } else if (strIcon.equals("icon_phonecall")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_phonecall);
//        } else if (strIcon.equals("icon_swim")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_swim);
//        } else if (strIcon.equals("icon_walk")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_walk);
//        } else if (strIcon.equals("icon_work")) {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_work);
//        } else {
//            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_sleep);
//        }
//    }

    public static int getIconResourceId(String strIcon) {

        return getResId(strIcon, R.drawable.class);

//        if (strIcon.equals(Constants.APP_ICON_SMALL)) {
//            return R.drawable.deteclife_icon_line;
//        } else if (strIcon.equals(Constants.APP_ICON_BIG)) {
//            return R.drawable.deteclife_icon_fill;
//
//        } else if (strIcon.equals("icon_sleep")) {
//            return R.drawable.icon_sleep;
//        } else if (strIcon.equals("icon_bike")) {
//            return R.drawable.icon_bike;
//        } else if (strIcon.equals("icon_book")) {
//            return R.drawable.icon_book;
//        } else if (strIcon.equals("icon_car")) {
//            return R.drawable.icon_car;
//        } else if (strIcon.equals("icon_computer")) {
//            return R.drawable.icon_computer;
//        } else if (strIcon.equals("icon_drunk")) {
//            return R.drawable.icon_drunk;
//        } else if (strIcon.equals("icon_friend")) {
//            return R.drawable.icon_friend;
//        } else if (strIcon.equals("icon_food")) {
//            return R.drawable.icon_food;
//        } else if (strIcon.equals("icon_home")) {
//            return R.drawable.icon_home;
//        } else if (strIcon.equals("icon_lover")) {
//            return R.drawable.icon_lover;
//        } else if (strIcon.equals("icon_music")) {
//            return R.drawable.icon_music;
//        } else if (strIcon.equals("icon_paw")) {
//            return R.drawable.icon_paw;
//        } else if (strIcon.equals("icon_phonecall")) {
//            return R.drawable.icon_phonecall;
//        } else if (strIcon.equals("icon_swim")) {
//            return R.drawable.icon_swim;
//        } else if (strIcon.equals("icon_walk")) {
//            return R.drawable.icon_walk;
//        } else if (strIcon.equals("icon_work")) {
//            return R.drawable.icon_work;
//        } else {
//            return R.drawable.icon_sleep;
//        }
    }

    public static boolean isFirstLogin() {
        return mIsFirstLogin;
    }

    public static void setFirstLogin(boolean isFirstLogin) {
        mIsFirstLogin = isFirstLogin;
    }


    public void firstLogin() {

        Logger.d(Constants.TAG, MSG + "First Login");
        // (0) greeting (?)
        // (1) prepare default tasks and categories
        // (2) tips
        // (x) prepare testing data
        // (x) save is_first_flag into shared-preferences


        // (1) prepare default tasks and categories
        prepareRoomDatabase();

        // (2) tips

        // once login, would save is_first_flag into shared-preferences
        mSharePreferences = getSharedPreferences(Constants.FILENAME_USER_DATA, Context.MODE_PRIVATE);
        mSharePreferences.edit()
                .putString(Constants.IS_FIRST_FLAG, "FALSE")
                .apply();
    }


    private void init() {

        // (3) set daily summary notificaitons
        // (4) set daily generate version job (for both planning data and tracing data)

        // (0) start foreground service for lock-screen listener
//        prepareRoomDatabaseTask();

        Logger.d(Constants.TAG, MSG + "start-service for broadcast-receiver of power-button");

        Intent intentService = new Intent(this, MainService.class);

        // after Android 8, need to start service at foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Logger.d(Constants.TAG, MSG + "start foreground service");
            startForegroundService(intentService);

        } else {

            Logger.d(Constants.TAG, MSG + "start background service");
            startService(intentService);
        }


        // (3) set daily summary notificaitons
        startJobScheduler(
                Constants.SCHEDULE_JOB_ID_DAILY_SUMMARY,
                JobSchedulerServiceDailySummary.class.getName(),
                ParseTime.getNextDailyNotifyMills(Constants.NOTIFICATION_TIME_DAILY_SUMMARY),
                true);

        // (4) set daily generate version job (for both planning data and tracing data)
        startJobScheduler(
                Constants.SCHEDULE_JOB_ID_DAILY_DATA_VERGEN,
                JobSchedulerServiceDailyDataVersionGeneration.class.getName(),
                ParseTime.getNextDailyNotifyMills(Constants.NOTIFICATION_TIME_DAILY_DATA_VERGEN),
                true);

        // [TODO] start job to notify remaning time of current task

    }

    private void prepareRoomDatabase() {

        // 和 Database 有關的操作不能放在 main-thread 中。不然會跳出錯誤：
        // Cannot access database on the main thread since it may potentially lock the UI for a long period of time.

        // 解決方式：(此處使用 2)
        // 1. 在取得資料庫連線時增加 allowMainThreadQueries() 方法，強制在主程式中執行
        // 2. 另開 thread 執行耗時工作 (建議採用此方法)，另開 thread 有多種寫法，按自己習慣作業即可。此處為測試是否寫入手機SQLite，故不考慮 callback，如下
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                DatabaseDao dao = AppDatabase.getDatabase(getAppContext()).getDatabaseDao();

                // 1. 取得時間 // [TODO] 未來要改取畫面上時間
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);

                // 1.1 做出新增 Task 的 startTime, endTime
                String curEndVerNo = Constants.DB_ENDLESS_DATE;

                // Daily 的 startTime 是當天
                String curStartVerNoDaily = simpleDateFormat.format(curDate);

                // Weekly 的 startTime 是當週的週一
                int intWeekDay = ParseTime.date2Day(curDate);    // 把今天傳入，回傳今天是星期幾 (1 = 星期一，2 = 星期二)
                // 如果今天是星期一，則需從今天往回減 0 天。
                // 如果今天是星期二，則需從今天往回減 1 天。
                Date thisMonday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (intWeekDay - 1)); // 找出本週一
                String curStartVerNoWeekly = simpleDateFormat.format(thisMonday);


                // 1.2 update_date
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleUpdateDateFormat.format(curDate);


                // 1.3 取得上一個 planning 週期的 endTime (daily 為昨天，weekly 為上週日)
                Date yesterday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
                String lastEndVerNoDaily = simpleDateFormat.format(yesterday);

                // 計算 Weekly 的上一個週期 endTime
                // 如果今天是星期一，則需從今天往回減 1 天。
                // 如果今天是星期二，則需從今天往回減 2 天。
                Date lastSunday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * intWeekDay); // 找出上週日
                String lastEndVerNoWeekly = simpleDateFormat.format(lastSunday);



                // Prepare sample plan
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Sleep", curStartVerNoDaily, curEndVerNo, 480 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Eat", curStartVerNoDaily, curEndVerNo, 120 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Family", "Family", curStartVerNoDaily, curEndVerNo, 120 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Personal", "Personal", curStartVerNoDaily, curEndVerNo, 60 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Friend", "Friend", curStartVerNoDaily, curEndVerNo, 60 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Swim", curStartVerNoDaily, curEndVerNo, 60 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Others", "Music", curStartVerNoDaily, curEndVerNo, 30 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Sleep", curStartVerNoWeekly, curEndVerNo,  3360 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Eat", curStartVerNoWeekly, curEndVerNo, 1200 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Family", "Family", curStartVerNoWeekly, curEndVerNo, 840 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Personal", "Personal", curStartVerNoWeekly, curEndVerNo, 420 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Friend", "Friend", curStartVerNoWeekly, curEndVerNo, 480 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Friend", "Beer", curStartVerNoWeekly, curEndVerNo, 120 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Others", "Shopping", curStartVerNoWeekly, curEndVerNo, 240 * 60000, strCurrentTime));

//                // Prepare default category
//                dao.addCategory(new CategoryDefineTable("Health", "#32CD32", 1, false));
//                dao.addCategory(new CategoryDefineTable("Family", "#C71585", 2, false));
//                dao.addCategory(new CategoryDefineTable("Personal", "#FFD700", 3, false));
//                dao.addCategory(new CategoryDefineTable("Friend", "#F4A460", 4, false));
//                dao.addCategory(new CategoryDefineTable("Work", "#1E90FF", 5, false));
//                dao.addCategory(new CategoryDefineTable("Transportation", "#B0C4DE", 6, false));
//                dao.addCategory(new CategoryDefineTable("Others", "#4682B4", 7, false));
//
//                // Prepare default task
//                dao.addTask(new TaskDefineTable("Work", "Work", "#4169E1", "icon_work", 8, false));
//                dao.addTask(new TaskDefineTable("Personal", "Study", "#008B8B", "icon_book", 7, false));
//                dao.addTask(new TaskDefineTable("Family", "Family", "#FF69B4", "icon_home", 4, false));
//                dao.addTask(new TaskDefineTable("Friend", "Friend", "#D2691E", "icon_friend", 6, false));
//                dao.addTask(new TaskDefineTable("Friend", "Beer", "#D2691E", "icon_beer", 6, false));
//                dao.addTask(new TaskDefineTable("Family", "Lover", "#C71585", "icon_lover", 5, false));
//                dao.addTask(new TaskDefineTable("Health", "Sleep", "#191970", "icon_sleep", 1, false));
//                dao.addTask(new TaskDefineTable("Health", "Eat", "#008B8B", "icon_food", 2, false));
//                dao.addTask(new TaskDefineTable("Health", "Swim", "#87CEFA", "icon_swim", 3, false));
//                dao.addTask(new TaskDefineTable("Transportation", "Walk", "#B0C4DE", "icon_walk", 13, false));
//                dao.addTask(new TaskDefineTable("Transportation", "Car", "#BDB76B", "icon_car", 15, false));
//                dao.addTask(new TaskDefineTable("Transportation", "Bike", "#3CB371", "icon_bike", 14, false));
//                dao.addTask(new TaskDefineTable("Others", "Computer", "#000000", "icon_computer", 12, false));
//                dao.addTask(new TaskDefineTable("Others", "Music", "#F08080", "icon_music", 10, false));
//                dao.addTask(new TaskDefineTable("Others", "Pet", "#FFB6C1", "icon_paw", 9, false));
//                dao.addTask(new TaskDefineTable("Others", "Shopping", "#FFB6C1", "icon_shopping", 9, false));
//                dao.addTask(new TaskDefineTable("Others", "Phone", "#FF6347", "icon_phonecall", 11, false));
//
//
//                // Prepare default task
//                dao.addIconItem(new IconDefineTable("icon_sleep", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_bike", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_book", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_car", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_computer", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_drunk", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_friend", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_food", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_home", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_lover", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_music", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_paw", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_phonecall", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_swim", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_walk", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_work", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_cook", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_chicken", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_medicine", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_ambulance", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_tooth", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_washingmachine", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_washtoilet", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_spray" , false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_plunger", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_tea", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_kettle", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_milk" , false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_beer", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_teaa", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_cocktail", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_weightlifting", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_dumbbell", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_gift", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_shopping", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_envelope", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_airplane", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_giveup_smoking", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_kiss", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_baby", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_game", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_camema", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_condom", false, strCurrentTime));
//                dao.addIconItem(new IconDefineTable("icon_guitar", false, strCurrentTime));

                // Prepare default category
                dao.addCategory(new CategoryDefineTable("Health", "#32CD32", 1, false));
                dao.addCategory(new CategoryDefineTable("Family", "#C71585", 2, false));
                dao.addCategory(new CategoryDefineTable("Personal", "#008B8B", 3, false));
                dao.addCategory(new CategoryDefineTable("Friend", "#F4A460", 4, false));
                dao.addCategory(new CategoryDefineTable("Work", "#1E90FF", 5, false));
                dao.addCategory(new CategoryDefineTable("Transportation", "#B0C4DE", 6, false));
                dao.addCategory(new CategoryDefineTable("Others", "#4682B4", 7, false));

                // Prepare default task
                dao.addTask(new TaskDefineTable("Work", "Work", "#2196f3", "icon_work", 8, false));
                dao.addTask(new TaskDefineTable("Personal", "Study", "#009688", "icon_book", 7, false));
                dao.addTask(new TaskDefineTable("Personal", "Personal", "#AA0000", "icon_personal", 8, false));
                dao.addTask(new TaskDefineTable("Family", "Family", "#FF69B4", "icon_home", 4, false));
                dao.addTask(new TaskDefineTable("Friend", "Friend", "#ba8df2", "icon_friend", 6, false));
                dao.addTask(new TaskDefineTable("Friend", "Beer", "#D2691E", "icon_beer", 7, false));
                dao.addTask(new TaskDefineTable("Family", "Lover", "#C71585", "icon_lover", 5, false));
                dao.addTask(new TaskDefineTable("Health", "Sleep", "#191970", "icon_sleep", 1, false));
                dao.addTask(new TaskDefineTable("Health", "Eat", "#ffc107", "icon_food", 2, false));
                dao.addTask(new TaskDefineTable("Health", "Swim", "#87CEFA", "icon_swim", 3, false));
                dao.addTask(new TaskDefineTable("Transportation", "Walk", "#8df2c1", "icon_walk", 13, false));
                dao.addTask(new TaskDefineTable("Transportation", "Car", "#3CB371", "icon_car", 15, false));
                dao.addTask(new TaskDefineTable("Transportation", "Bike", "#B0C4DE", "icon_bike", 14, false));
                dao.addTask(new TaskDefineTable("Others", "Computer", "#000000", "icon_computer", 12, false));
                dao.addTask(new TaskDefineTable("Others", "Music", "#F08080", "icon_music", 10, false));
                dao.addTask(new TaskDefineTable("Others", "Pet", "#FFB6C1", "icon_paw", 9, false));
                dao.addTask(new TaskDefineTable("Others", "Shopping", "#4B0082", "icon_shopping", 11, false));
                dao.addTask(new TaskDefineTable("Others", "Phone", "#FF6347", "icon_phonecall", 13, false));


                // Prepare default icon
                dao.addIconItem(new IconDefineTable("icon_sleep", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_bike", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_book", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_car", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_computer", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_drunk", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_friend", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_personal", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_food", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_home", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_lover", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_music", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_paw", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_phonecall", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_swim", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_walk", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_work", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_cook", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_chicken", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_medicine", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_ambulance", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_tooth", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_washingmachine", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_washtoilet", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_spray" , false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_plunger", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_tea", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_kettle", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_milk" , false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_beer", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_teaa", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_cocktail", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_weightlifting", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_dumbbell", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_gift", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_shopping", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_envelope", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_airplane", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_giveup_smoking", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_kiss", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_baby", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_game", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_camema", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_condom", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_guitar", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_plant", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_taxi", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_tools", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_caroil", false, strCurrentTime));


                // Prepare first trace
                dao.addTraceItem(new TimeTracingTable(curStartVerNoDaily, "Personal", "Personal",  curDate.getTime(), null, null, strCurrentTime));

                // [QUERY]
                // 可以在這邊撈，目前寫在這邊可以撈出來當前塞進去的資料。
                List<CategoryDefineTable> categoryList = dao.getAllCategoryList();
                List<TaskDefineTable> taskList = dao.getAllTaskList();
                List<TimePlanningTable> planningTableList = dao.getAllPlanList();
                List<TimeTracingTable> traceList = dao.getAllTraceList();

                Logger.d(Constants.TAG, MSG + "Prepare default category");
                for (int i = 0 ; i < categoryList.size() ; ++i) {
                    categoryList.get(i).LogD();
                }

                Logger.d(Constants.TAG, MSG + "Prepare default task");
                for (int i = 0 ; i < taskList.size() ; ++i) {
                    taskList.get(i).LogD();
                }

                Logger.d(Constants.TAG, MSG + "Prepare sample plan");
                for (int i = 0 ; i < planningTableList.size() ; ++i) {
                    planningTableList.get(i).LogD();
                }

                Logger.d(Constants.TAG, MSG + "Prepare first trace");
                for (int i = 0 ; i < traceList.size() ; ++i) {
                    traceList.get(i).LogD();
                }

            }
        });
    }



    private void prepareRoomDatabaseTask() {

        // 和 Database 有關的操作不能放在 main-thread 中。不然會跳出錯誤：
        // Cannot access database on the main thread since it may potentially lock the UI for a long period of time.

        // 解決方式：(此處使用 2)
        // 1. 在取得資料庫連線時增加 allowMainThreadQueries() 方法，強制在主程式中執行
        // 2. 另開 thread 執行耗時工作 (建議採用此方法)，另開 thread 有多種寫法，按自己習慣作業即可。此處為測試是否寫入手機SQLite，故不考慮 callback，如下
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                DatabaseDao dao = AppDatabase.getDatabase(getAppContext()).getDatabaseDao();

                // 1. 取得現在時間
                // 1.1 做成 startTime, endTime
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strVerNo = simpleDateFormat.format(curDate);
                String strStartTime = simpleDateFormat.format(curDate);
                String curEndVerNo = Constants.DB_ENDLESS_DATE;

                // 1.2 update_date
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleUpdateDateFormat.format(curDate);



                Date yesterday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
                Date lastday1 = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2);
                Date lastday2 = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3);
                Date lastday3 = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 4);
                Date lastday4 = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 5);
                Date lastday5 = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 6);
                Date lastday6 = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7);
                String lastEndVerNoDaily = simpleDateFormat.format(yesterday);


                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Sleep", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 420 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Eat", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 120 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Family", "Family", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 120 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Personal", "Study", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 75 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Friend", "Friend", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 60 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Swim", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 60 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Others", "Music", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 30 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Sleep", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 2940 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Eat", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 1200 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Family", "Family", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 1800 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Personal", "Study", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 1200 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Friend", "Friend", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 480 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Friend", "Beer", simpleDateFormat.format(lastday6), simpleDateFormat.format(yesterday), 120 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Others", "Shopping", strStartTime, simpleDateFormat.format(yesterday), 240 * 60000, strCurrentTime));



                // 計算 Weekly 的上一個週期 endTime
                // 如果今天是星期一，則需從今天往回減 1 天。
                // 如果今天是星期二，則需從今天往回減 2 天。
                Date lastSunday = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24); // 找出上週日
                String lastEndVerNoWeekly = simpleDateFormat.format(lastSunday);

                SimpleDateFormat dateStringFormat = new SimpleDateFormat( Constants.DB_FORMAT_VER_NO );

                try {
                    Date date = dateStringFormat.parse( strVerNo );

                    // Prepare last week data
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Health", "Sleep",  yesterday.getTime(), yesterday.getTime() + (300 * 60000), (long) 300 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Family", "Family",  yesterday.getTime(), yesterday.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Personal", "Study",  yesterday.getTime(), yesterday.getTime() + (120 * 60000), (long) 120 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Health", "Eat",  yesterday.getTime(), yesterday.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Health", "Swim",  yesterday.getTime(), yesterday.getTime() + (75 * 60000), (long) 75 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Family", "Lover",  yesterday.getTime(), yesterday.getTime() + (90 * 60000), (long) 90 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Transportation", "Walk",  yesterday.getTime(), yesterday.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Others", "Computer",  yesterday.getTime(), yesterday.getTime() + (240 * 60000), (long) 240 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(yesterday), "Others", "Music",  yesterday.getTime(), yesterday.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));

                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Health", "Sleep",  lastday1.getTime(), lastday1.getTime() + (300 * 60000), (long) 300 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Family", "Family",  lastday1.getTime(), lastday1.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Personal", "Study",  lastday1.getTime(), lastday1.getTime() + (120 * 60000), (long) 120 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Health", "Eat",  lastday1.getTime(), lastday1.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Health", "Swim",  lastday1.getTime(), lastday1.getTime() + (75 * 60000), (long) 75 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Family", "Lover",  lastday1.getTime(), lastday1.getTime() + (90 * 60000), (long) 90 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Transportation", "Walk",  lastday1.getTime(), lastday1.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Others", "Computer",  lastday1.getTime(), lastday1.getTime() + (240 * 60000), (long) 240 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday1), "Others", "Music",  lastday1.getTime(), lastday1.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));

                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Health", "Sleep",  lastday2.getTime(), lastday2.getTime() + (300 * 60000), (long) 300 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Family", "Family",  lastday2.getTime(), lastday2.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Personal", "Study",  lastday2.getTime(), lastday2.getTime() + (120 * 60000), (long) 120 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Health", "Eat",  lastday2.getTime(), lastday2.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Health", "Swim",  lastday2.getTime(), lastday2.getTime() + (75 * 60000), (long) 75 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Family", "Lover",  lastday2.getTime(), lastday2.getTime() + (90 * 60000), (long) 90 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Transportation", "Walk",  lastday2.getTime(), lastday2.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Others", "Computer",  lastday2.getTime(), lastday2.getTime() + (240 * 60000), (long) 240 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday2), "Others", "Music",  lastday2.getTime(), lastday2.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));

                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Health", "Sleep",  lastday3.getTime(), lastday3.getTime() + (300 * 60000), (long) 300 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Family", "Family",  lastday3.getTime(), lastday3.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Personal", "Study",  lastday3.getTime(), lastday3.getTime() + (120 * 60000), (long) 120 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Health", "Eat",  lastday3.getTime(), lastday3.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Health", "Swim",  lastday3.getTime(), lastday3.getTime() + (75 * 60000), (long) 75 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Family", "Lover",  lastday3.getTime(), lastday3.getTime() + (90 * 60000), (long) 90 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Transportation", "Walk",  lastday3.getTime(), lastday3.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Others", "Computer",  lastday3.getTime(), lastday3.getTime() + (240 * 60000), (long) 240 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday3), "Others", "Music",  lastday3.getTime(), lastday3.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));

                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Health", "Sleep",  lastday4.getTime(), lastday4.getTime() + (300 * 60000), (long) 300 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Family", "Family",  lastday4.getTime(), lastday4.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Personal", "Study",  lastday4.getTime(), lastday4.getTime() + (120 * 60000), (long) 120 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Health", "Eat",  lastday4.getTime(), lastday4.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Health", "Swim",  lastday4.getTime(), lastday4.getTime() + (75 * 60000), (long) 75 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Family", "Lover",  lastday4.getTime(), lastday4.getTime() + (90 * 60000), (long) 90 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Transportation", "Walk",  lastday4.getTime(), lastday4.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Others", "Computer",  lastday4.getTime(), lastday4.getTime() + (240 * 60000), (long) 240 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday4), "Others", "Music",  lastday4.getTime(), lastday4.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));

                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Health", "Sleep",  lastday5.getTime(), lastday5.getTime() + (300 * 60000), (long) 300 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Family", "Family",  lastday5.getTime(), lastday5.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Personal", "Study",  lastday5.getTime(), lastday5.getTime() + (120 * 60000), (long) 120 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Health", "Eat",  lastday5.getTime(), lastday5.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Health", "Swim",  lastday5.getTime(), lastday5.getTime() + (75 * 60000), (long) 75 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Family", "Lover",  lastday5.getTime(), lastday5.getTime() + (90 * 60000), (long) 90 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Transportation", "Walk",  lastday5.getTime(), lastday5.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Others", "Computer",  lastday5.getTime(), lastday5.getTime() + (240 * 60000), (long) 240 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday5), "Others", "Music",  lastday5.getTime(), lastday5.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));

                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Health", "Sleep",  lastday6.getTime(), lastday6.getTime() + (300 * 60000), (long) 300 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Family", "Family",  lastday6.getTime(), lastday6.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Personal", "Study",  lastday6.getTime(), lastday6.getTime() + (120 * 60000), (long) 120 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Health", "Eat",  lastday6.getTime(), lastday6.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Health", "Swim",  lastday6.getTime(), lastday6.getTime() + (75 * 60000), (long) 75 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Family", "Lover",  lastday6.getTime(), lastday6.getTime() + (90 * 60000), (long) 90 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Transportation", "Walk",  lastday6.getTime(), lastday6.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Others", "Computer",  lastday6.getTime(), lastday6.getTime() + (240 * 60000), (long) 240 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(simpleDateFormat.format(lastday6), "Others", "Music",  lastday6.getTime(), lastday6.getTime() + (30 * 60000), (long) 30 * 60000, strCurrentTime));


                    // Prepare trace data
                    dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Sleep",  date.getTime(), date.getTime() + (300 * 60000), (long) 300 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(strVerNo, "Family", "Family",  date.getTime(), date.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(strVerNo, "Personal", "Study",  date.getTime(), date.getTime() + (120 * 60000), (long) 120 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Eat",  date.getTime(), date.getTime() + (60 * 60000), (long) 60 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Swim",  date.getTime(), date.getTime() + (75 * 60000), (long) 75 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(strVerNo, "Friend", "Friend",  date.getTime(), date.getTime() + (90 * 60000), (long) 90 * 60000, strCurrentTime));
                    dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Sleep",  curDate.getTime(), null, null, strCurrentTime));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Prepare first trace
//                dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Sleep",  curDate.getTime(), curDate.getTime() + (300 * 60000), null, strCurrentTime));
//                dao.addTraceItem(new TimeTracingTable(strVerNo, "Family", "Family",  curDate.getTime(), curDate.getTime() + (60 * 60000), null, strCurrentTime));
//                dao.addTraceItem(new TimeTracingTable(strVerNo, "Personal", "Study",  curDate.getTime(), curDate.getTime() + (120 * 60000), null, strCurrentTime));
//                dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Eat",  curDate.getTime(), curDate.getTime() + (60 * 60000), null, strCurrentTime));
//                dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Swim",  curDate.getTime(), curDate.getTime() + (300 * 60000), null, strCurrentTime));





                // Prepare default category
                dao.addCategory(new CategoryDefineTable("Health", "#32CD32", 1, false));
                dao.addCategory(new CategoryDefineTable("Family", "#C71585", 2, false));
                dao.addCategory(new CategoryDefineTable("Personal", "#008B8B", 3, false));
                dao.addCategory(new CategoryDefineTable("Friend", "#F4A460", 4, false));
                dao.addCategory(new CategoryDefineTable("Work", "#1E90FF", 5, false));
                dao.addCategory(new CategoryDefineTable("Transportation", "#B0C4DE", 6, false));
                dao.addCategory(new CategoryDefineTable("Others", "#4682B4", 7, false));

                // Prepare default task
                dao.addTask(new TaskDefineTable("Work", "Work", "#2196f3", "icon_work", 8, false));
                dao.addTask(new TaskDefineTable("Personal", "Study", "#009688", "icon_book", 7, false));
                dao.addTask(new TaskDefineTable("Family", "Family", "#FF69B4", "icon_home", 4, false));
                dao.addTask(new TaskDefineTable("Friend", "Friend", "#ba8df2", "icon_friend", 6, false));
                dao.addTask(new TaskDefineTable("Friend", "Beer", "#D2691E", "icon_beer", 7, false));
                dao.addTask(new TaskDefineTable("Family", "Lover", "#C71585", "icon_lover", 5, false));
                dao.addTask(new TaskDefineTable("Health", "Sleep", "#191970", "icon_sleep", 1, false));
                dao.addTask(new TaskDefineTable("Health", "Eat", "#ffc107", "icon_food", 2, false));
                dao.addTask(new TaskDefineTable("Health", "Swim", "#87CEFA", "icon_swim", 3, false));
                dao.addTask(new TaskDefineTable("Transportation", "Walk", "#8df2c1", "icon_walk", 13, false));
                dao.addTask(new TaskDefineTable("Transportation", "Car", "#3CB371", "icon_car", 15, false));
                dao.addTask(new TaskDefineTable("Transportation", "Bike", "#B0C4DE", "icon_bike", 14, false));
                dao.addTask(new TaskDefineTable("Others", "Computer", "#000000", "icon_computer", 12, false));
                dao.addTask(new TaskDefineTable("Others", "Music", "#F08080", "icon_music", 10, false));
                dao.addTask(new TaskDefineTable("Others", "Pet", "#FFB6C1", "icon_paw", 9, false));
                dao.addTask(new TaskDefineTable("Others", "Shopping", "#4B0082", "icon_shopping", 11, false));
                dao.addTask(new TaskDefineTable("Others", "Phone", "#FF6347", "icon_phonecall", 13, false));


                // Prepare default icon
                dao.addIconItem(new IconDefineTable("icon_sleep", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_bike", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_book", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_car", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_computer", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_drunk", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_friend", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_food", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_home", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_lover", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_music", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_paw", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_phonecall", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_swim", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_walk", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_work", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_cook", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_chicken", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_medicine", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_ambulance", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_tooth", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_washingmachine", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_washtoilet", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_spray" , false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_plunger", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_tea", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_kettle", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_milk" , false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_beer", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_teaa", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_cocktail", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_weightlifting", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_dumbbell", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_gift", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_shopping", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_envelope", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_airplane", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_giveup_smoking", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_kiss", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_baby", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_game", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_camema", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_condom", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_guitar", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_plant", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_taxi", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_tools", false, strCurrentTime));
                dao.addIconItem(new IconDefineTable("icon_caroil", false, strCurrentTime));

                // Prepare sample plan
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Sleep", strStartTime, curEndVerNo, 480 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Eat", strStartTime, curEndVerNo, 120 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Family", "Family", strStartTime, curEndVerNo, 120 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Personal", "Study", strStartTime, curEndVerNo, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Friend", "Friend", strStartTime, curEndVerNo, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Swim", strStartTime, curEndVerNo, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Others", "Music", strStartTime, curEndVerNo, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Sleep", strStartTime, curEndVerNo, 4800 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Eat", strStartTime, curEndVerNo, 1200 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Family", "Family", strStartTime, curEndVerNo, 1200 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Personal", "Study", strStartTime, curEndVerNo, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Friend", "Friend", strStartTime, curEndVerNo, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Swim", strStartTime, curEndVerNo, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Others", "Music", strStartTime, curEndVerNo, 75 * 60000, strCurrentTime));



                // [QUERY]
                // 可以在這邊撈，目前寫在這邊可以撈出來當前塞進去的資料。
                List<CategoryDefineTable> categoryList = dao.getAllCategoryList();
                List<TaskDefineTable> taskList = dao.getAllTaskList();
                List<TimePlanningTable> planningTableList = dao.getAllPlanList();
                List<TimeTracingTable> traceList = dao.getAllTraceList();

                Logger.d(Constants.TAG, MSG + "Prepare default category");
                for (int i = 0 ; i < categoryList.size() ; ++i) {
                    categoryList.get(i).LogD();
                }

                Logger.d(Constants.TAG, MSG + "Prepare default task");
                for (int i = 0 ; i < taskList.size() ; ++i) {
                    taskList.get(i).LogD();
                }

                Logger.d(Constants.TAG, MSG + "Prepare sample plan");
                for (int i = 0 ; i < planningTableList.size() ; ++i) {
                    planningTableList.get(i).LogD();
                }

                Logger.d(Constants.TAG, MSG + "Prepare first trace");
                for (int i = 0 ; i < traceList.size() ; ++i) {
                    traceList.get(i).LogD();
                }

            }
        });
    }



    // ****** Create JobScheduler to start a schedule job ******
    // (start JobSchedulerService at specific time to create a notification)

    public static void startJobScheduler(int jobId, String strClassName, long longLatencyMills, boolean isPersisted) {

        Logger.d(Constants.TAG, MSG + "Start scheduling job");

        ComponentName componentName = new ComponentName(mContext, strClassName);   // service name

        JobInfo jobInfo = new JobInfo.Builder(jobId, componentName)
//                .setPeriodic(10 * 1000)
                .setMinimumLatency(longLatencyMills)
                .setOverrideDeadline(longLatencyMills)
                .setPersisted(isPersisted)     // 為了讓重開機還能繼續執行此 job
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) // 只有網路不限流量時 (e.g. WIFI)
//                .setRequiresDeviceIdle(false)
//                .setRequiresCharging(false)
                .build();

        JobScheduler scheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        int result = scheduler.schedule(jobInfo);   // start a jobScheduler task, return successful job id (return 0 if failed)

        if (result == JobScheduler.RESULT_SUCCESS) {
            Logger.d(Constants.TAG, MSG + "Job scheduled successfully: " + strClassName);
        }
    }

    private void cancelJobScheduler(int jobId) {

        Logger.d(Constants.TAG, MSG + "Cancel scheduling job");

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(jobId);

        Logger.d(Constants.TAG, MSG + "Cancel job: id = " + jobId + " successfully!");
    }

    private void cancelAllJobScheduler() {

        Logger.d(Constants.TAG, MSG + "Cancel all scheduling job");

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        List<JobInfo> allPendingJobs = scheduler.getAllPendingJobs();
        for (JobInfo info : allPendingJobs) {
            int id = info.getId();
            scheduler.cancel(id);
        }
        //or
        // scheduler.cancelAll();

        Logger.d(Constants.TAG, MSG + "Cancel all scheduled jobs successfully!");

    }



}
