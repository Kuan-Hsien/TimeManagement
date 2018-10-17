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

        // [IS_FIRST_FLAG = true] if first login
        if ("TRUE".equals(
                getSharedPreferences(Constants.FILENAME_USER_DATA, Context.MODE_PRIVATE)
                        .getString(Constants.IS_FIRST_FLAG, "TRUE"))) {

            setFirstLogin(true);
            firstLogin();

        } else {

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



    public static Drawable getIconResourceDrawable(String strIcon) {

        if (strIcon.equals(Constants.APP_ICON_SMALL)) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.deteclife_icon_line);
        } else if (strIcon.equals(Constants.APP_ICON_BIG)) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.deteclife_icon_fill);

        } else if (strIcon.equals("icon_sleep")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_sleep);
        } else if (strIcon.equals("icon_bike")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_bike);
        } else if (strIcon.equals("icon_book")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_book);
        } else if (strIcon.equals("icon_car")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_car);
        } else if (strIcon.equals("icon_computer")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_computer);
        } else if (strIcon.equals("icon_drunk")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_drunk);
        } else if (strIcon.equals("icon_friend")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_friend);
        } else if (strIcon.equals("icon_food")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_food);
        } else if (strIcon.equals("icon_home")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_home);
        } else if (strIcon.equals("icon_lover")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_lover);
        } else if (strIcon.equals("icon_music")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_music);
        } else if (strIcon.equals("icon_paw")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_paw);
        } else if (strIcon.equals("icon_phonecall")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_phonecall);
        } else if (strIcon.equals("icon_swim")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_swim);
        } else if (strIcon.equals("icon_walk")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_walk);
        } else if (strIcon.equals("icon_work")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_work);
        } else {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_sleep);
        }
    }

    public static int getIconResourceId(String strIcon) {

        if (strIcon.equals(Constants.APP_ICON_SMALL)) {
            return R.drawable.deteclife_icon_line;
        } else if (strIcon.equals(Constants.APP_ICON_BIG)) {
            return R.drawable.deteclife_icon_fill;

        } else if (strIcon.equals("icon_sleep")) {
            return R.drawable.icon_sleep;
        } else if (strIcon.equals("icon_bike")) {
            return R.drawable.icon_bike;
        } else if (strIcon.equals("icon_book")) {
            return R.drawable.icon_book;
        } else if (strIcon.equals("icon_car")) {
            return R.drawable.icon_car;
        } else if (strIcon.equals("icon_computer")) {
            return R.drawable.icon_computer;
        } else if (strIcon.equals("icon_drunk")) {
            return R.drawable.icon_drunk;
        } else if (strIcon.equals("icon_friend")) {
            return R.drawable.icon_friend;
        } else if (strIcon.equals("icon_food")) {
            return R.drawable.icon_food;
        } else if (strIcon.equals("icon_home")) {
            return R.drawable.icon_home;
        } else if (strIcon.equals("icon_lover")) {
            return R.drawable.icon_lover;
        } else if (strIcon.equals("icon_music")) {
            return R.drawable.icon_music;
        } else if (strIcon.equals("icon_paw")) {
            return R.drawable.icon_paw;
        } else if (strIcon.equals("icon_phonecall")) {
            return R.drawable.icon_phonecall;
        } else if (strIcon.equals("icon_swim")) {
            return R.drawable.icon_swim;
        } else if (strIcon.equals("icon_walk")) {
            return R.drawable.icon_walk;
        } else if (strIcon.equals("icon_work")) {
            return R.drawable.icon_work;
        } else {
            return R.drawable.icon_sleep;
        }
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
        prepareRoomDatabaseTask();

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

                // 1. 取得現在時間
                // 1.1 做成 startTime, endTime
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strVerNo = simpleDateFormat.format(curDate);
                String strStartTime = simpleDateFormat.format(curDate);
                String strEndTime = Constants.DB_ENDLESS_DATE;

                // 1.2 update_date
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleUpdateDateFormat.format(curDate);

                // Prepare sample plan
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Sleep", strStartTime, strEndTime, 480 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Eat", strStartTime, strEndTime, 120 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Family", "Family", strStartTime, strEndTime, 120 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Personal", "Study", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Friend", "Friend", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Swim", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Others", "Music", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Sleep", strStartTime, strEndTime, 480 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Eat", strStartTime, strEndTime, 1200 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Family", "Family", strStartTime, strEndTime, 1200 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Personal", "Study", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Friend", "Friend", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Swim", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Others", "Music", strStartTime, strEndTime, 75 * 60000, strCurrentTime));

                // Prepare default category
                dao.addCategory(new CategoryDefineTable("Health", false, "#32CD32", 1));
                dao.addCategory(new CategoryDefineTable("Family", false, "#C71585", 2));
                dao.addCategory(new CategoryDefineTable("Personal", false, "#FFD700", 3));
                dao.addCategory(new CategoryDefineTable("Friend", false, "#F4A460", 4));
                dao.addCategory(new CategoryDefineTable("Work", false, "#1E90FF", 5));
                dao.addCategory(new CategoryDefineTable("Transportation", false, "#B0C4DE", 6));
                dao.addCategory(new CategoryDefineTable("Others", false, "#4682B4", 7));

                // Prepare default task
                dao.addTask(new TaskDefineTable("Work", "Work", "#4169E1", "icon_work", 8, false));
                dao.addTask(new TaskDefineTable("Personal", "Study", "#008B8B", "icon_book", 7, false));
                dao.addTask(new TaskDefineTable("Family", "Family", "#FF69B4", "icon_home", 4, false));
                dao.addTask(new TaskDefineTable("Friend", "Friend", "#D2691E", "icon_friend", 6, false));
                dao.addTask(new TaskDefineTable("Family", "Lover", "#C71585", "icon_lover", 5, false));
                dao.addTask(new TaskDefineTable("Health", "Sleep", "#191970", "icon_sleep", 1, false));
                dao.addTask(new TaskDefineTable("Health", "Eat", "#008B8B", "icon_food", 2, false));
                dao.addTask(new TaskDefineTable("Health", "Swim", "#87CEFA", "icon_swim", 3, false));
                dao.addTask(new TaskDefineTable("Transportation", "Walk", "#B0C4DE", "icon_walk", 13, false));
                dao.addTask(new TaskDefineTable("Transportation", "Car", "#BDB76B", "icon_car", 15, false));
                dao.addTask(new TaskDefineTable("Transportation", "Bike", "#3CB371", "icon_bike", 14, false));
                dao.addTask(new TaskDefineTable("Others", "Computer", "#000000", "icon_computer", 12, false));
                dao.addTask(new TaskDefineTable("Others", "Music", "#F08080", "icon_music", 10, false));
                dao.addTask(new TaskDefineTable("Others", "Pet", "#FFB6C1", "icon_paw", 9, false));
                dao.addTask(new TaskDefineTable("Others", "Phone", "#FF6347", "icon_phonecall", 11, false));

                // Prepare first trace
                dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Sleep",  curDate.getTime(), null, null, strCurrentTime));

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
                String strEndTime = Constants.DB_ENDLESS_DATE;

                // 1.2 update_date
                SimpleDateFormat simpleUpdateDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleUpdateDateFormat.format(curDate);



                // Prepare default category
                dao.addCategory(new CategoryDefineTable("Health", false, "#32CD32", 1));
                dao.addCategory(new CategoryDefineTable("Family", false, "#C71585", 2));
                dao.addCategory(new CategoryDefineTable("Personal", false, "#FFD700", 3));
                dao.addCategory(new CategoryDefineTable("Friend", false, "#F4A460", 4));
                dao.addCategory(new CategoryDefineTable("Work", false, "#1E90FF", 5));
                dao.addCategory(new CategoryDefineTable("Transportation", false, "#B0C4DE", 6));
                dao.addCategory(new CategoryDefineTable("Others", false, "#4682B4", 7));

                // Prepare default task
                dao.addTask(new TaskDefineTable("Work", "Work", "#2196f3", "icon_work", 8, false));
                dao.addTask(new TaskDefineTable("Personal", "Study", "#009688", "icon_book", 7, false));
                dao.addTask(new TaskDefineTable("Family", "Family", "#FF69B4", "icon_home", 4, false));
                dao.addTask(new TaskDefineTable("Friend", "Friend", "#ba8df2", "icon_friend", 6, false));
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
                dao.addTask(new TaskDefineTable("Others", "Phone", "#FF6347", "icon_phonecall", 11, false));

                // Prepare sample plan
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Sleep", strStartTime, strEndTime, 480 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Eat", strStartTime, strEndTime, 120 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Family", "Family", strStartTime, strEndTime, 120 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Personal", "Study", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Friend", "Friend", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Swim", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Others", "Music", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Sleep", strStartTime, strEndTime, 4800 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Eat", strStartTime, strEndTime, 1200 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Family", "Family", strStartTime, strEndTime, 1200 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Personal", "Study", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Friend", "Friend", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Health", "Swim", strStartTime, strEndTime, 75 * 60000, strCurrentTime));
//                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Others", "Music", strStartTime, strEndTime, 75 * 60000, strCurrentTime));

                // Prepare first trace
//                dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Sleep",  curDate.getTime(), null, null, strCurrentTime));

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
