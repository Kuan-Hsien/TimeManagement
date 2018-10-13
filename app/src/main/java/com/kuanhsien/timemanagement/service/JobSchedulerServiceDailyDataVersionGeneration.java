package com.kuanhsien.timemanagement.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.kuanhsien.timemanagement.MainActivity;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.dml.GetCurrentTraceTaskAsyncTask;
import com.kuanhsien.timemanagement.dml.GetCurrentTraceTaskCallback;
import com.kuanhsien.timemanagement.dml.GetResultDailySummary;
import com.kuanhsien.timemanagement.dml.GetResultDailySummaryAsyncTask;
import com.kuanhsien.timemanagement.dml.GetResultDailySummaryCallback;
import com.kuanhsien.timemanagement.dml.GetTraceSummary;
import com.kuanhsien.timemanagement.dml.SetRecordAsyncTask;
import com.kuanhsien.timemanagement.dml.SetRecordCallback;
import com.kuanhsien.timemanagement.object.TimeTracingTable;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;
import com.kuanhsien.timemanagement.utils.ParseTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ken on 2018/10/13
 *
 * JobService 在 main thread 中執行，所有耗时邏輯應寫在背景執行緒中
 */
public class JobSchedulerServiceDailyDataVersionGeneration extends JobService {

    private static final String MSG = "JobSchedulerServiceDailyDataVersionGeneration: ";

    private boolean mLoading = false;


    public JobSchedulerServiceDailyDataVersionGeneration() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(Constants.TAG, MSG + "onCreate");
    }

    /*
        START_STICKY : Service 被殺掉, 系統會重啟, 但是 Intent 會是 null。
        START_NOT_STICKY : Service 被系統殺掉, 不會重啟。
        START_REDELIVER_INTENT : Service 被系統殺掉, 重啟且 Intent 會重傳。
        透過以上的參數, 放在 onStartCommand 的 return 參數就可以使用重啟的功能了。
    */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Logger.i(Constants.TAG, MSG + "onStartCommand");

        return START_REDELIVER_INTENT;
    }

    // 返回 true，表示該工作耗時，工作處理完成後需要调用 onStopJob 销毁（jobFinished）
    // 返回 false，代表任務不需要很長時間，到 return 時已完成任務處理
    @Override
    public boolean onStartJob(JobParameters params) {
        Logger.i(Constants.TAG, MSG + "onStartJob");

        // （Method-1) 昨天日期
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, -1);
//        Date date = calendar.getTime();

        // （Method-2) 昨天日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
        Date date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        String strYesterdayVerNo = simpleDateFormat.format(date);

        // (1) get current tracing task (last task yesterday)
        // (2) save that tracing and create a new trace today with the same task/category
        saveAndCreateNewRecordDaily(params, strYesterdayVerNo);


        return true;
    }

    // 有且僅有 onStartJob 返回值為 true 時，才會調用 onStopJob 銷毀 job
    // 返回 false 來直接銷毀這個工作
    @Override
    public boolean onStopJob(JobParameters params) {
        Logger.i(Constants.TAG, MSG + "onStopJob");
        return false;
    }

    // (1) get current tracing task (last task yesterday) (get bean)
    public void saveAndCreateNewRecordDaily(final JobParameters params, String strYesterdayVerNo) {

        if (!isLoading()) {
            setLoading(true);

            new GetCurrentTraceTaskAsyncTask(strYesterdayVerNo, new GetCurrentTraceTaskCallback() {

                @Override
                public void onCompleted(TimeTracingTable bean) {
                    setLoading(false);
                    setRecordDaily(params, bean);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, MSG + "GetGetCurrentTraceTask.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }

    // (2) save that tracing and create a new trace today with the same task/category
    private void setRecordDaily(final JobParameters params, TimeTracingTable bean) {

        // (2-0) 組出今天日期 + 版號 (yyyy/MM/dd HH:mm:ss) => newDate
        // current time as startTime
        Date curDate = new Date();

        // format
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(Constants.DB_FORMAT_UPDATE_DATE);

        // 透過SimpleDateFormat的format方法將 Date 轉為字串
        String strTodayVer = simpleDateFormatDate.format(curDate); // yyyy/MM/dd
        String strTodayTime = simpleDateFormatDate.format(curDate) + Constants.NOTIFICATION_TIME_DAILY_DATA_VERGEN; // "yyyy/MM/dd 00:00:00"


        // 字串轉回 Date，為了可以精確的算出毫秒
        Date today = null;

        try {
            Logger.d(Constants.TAG, MSG + "Today Ver: " + strTodayVer + " Time: " + strTodayTime);

            today = simpleDateFormatTime.parse(strTodayTime);

        } catch (ParseException e) {

            Logger.d(Constants.TAG, MSG + "Exception while parsing nextTime");

            today = curDate;
            e.printStackTrace();
        }

        Long longTodayTime = today.getTime(); // 把毫秒 (Long) 存起來


        // **
        List<TimeTracingTable> itemList = new ArrayList<>();

        // (2-1) insert 上一筆的 endtime + costtime (把前一天最後一筆存起來)
        TimeTracingTable lastItem = new TimeTracingTable(
                bean.getVerNo(),
                bean.getCategoryName(),
                bean.getTaskName(),
                bean.getStartTime(),
                longTodayTime,
                longTodayTime - bean.getStartTime(),
                strTodayTime
        );
        Logger.d(Constants.TAG, MSG + "save current task yesterday: ");
        lastItem.LogD();
        itemList.add(lastItem);

        // (2-2) insert 新一筆但沒有 endtime
        TimeTracingTable newItem = new TimeTracingTable(
                strTodayVer,
                bean.getCategoryName(),
                bean.getTaskName(),
                longTodayTime,
                null,
                null,
                strTodayTime
        );
        Logger.d(Constants.TAG, MSG + "start new task: ");
        newItem.LogD();
        itemList.add(newItem);


        // (2-3) 塞進資料庫 (insert time_tracing_table)
        new SetRecordAsyncTask(itemList, strTodayVer, strTodayVer, bean.getCategoryName(), bean.getTaskName(), new SetRecordCallback() {

            // after insert a new record, need to get the current trace summary and show in notification
            @Override
            public void onCompleted(List<GetTraceSummary> bean) {

                Logger.d(Constants.TAG, MSG + "SetRecord onCompleted");
                for( int i = 0 ; i < bean.size() ; ++i) {
                    bean.get(i).LogD();
                }

                setLoading(false);

                // (2-3-1) 算出下次通知時間並註冊 Job
                TimeManagementApplication.startJobScheduler(
                        Constants.SCHEDULE_JOB_ID_DAILY_DATA_VERGEN,
                        JobSchedulerServiceDailyDataVersionGeneration.class.getName(),
                        ParseTime.getNextDailyNotifyMills(Constants.NOTIFICATION_TIME_DAILY_DATA_VERGEN),
                        true);

                // (2-3-2) 啟動相關通知 [TODO] 像是開始任務時的 notification

                // (2-3-3) 結束當前 job
                jobFinished(params, false);
            }

            @Override
            public void onError(String errorMessage) {
                Logger.e(Constants.TAG, MSG + "SetRecord onError, errorMessage: " + errorMessage);
            }
        }).execute();

    }


    // 立即發送一個 notification
    private void startNotification(List<GetResultDailySummary> bean, String strTitle, String strSubText, String strContent) {

        // Large Icon 作法 1
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher_xxhdpi);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Large Icon 作法 2
//                Bitmap largeIcon = BitmapFactory.decodeResource(
//                        getResources(), R.drawable.icon_sleep);

//                // 建立通知物件 作法 1
//                Notification notification = builder.build();
//                // 使用BASIC_ID為編號發出通知
//                manager.notify(BASIC_ID, notification);

        // 建立通知物件 作法 2: 透過 builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_SUMMARY);

        // 設置小圖標
        mBuilder.setSmallIcon(R.drawable.btn_like_selected);
        // 設置大圖標
        mBuilder.setLargeIcon(bitmap);

        // 標題
        mBuilder.setContentTitle(strTitle);
        // 內容
        mBuilder.setContentText(strContent);
        // 摘要
        mBuilder.setSubText(strSubText);

        // 點擊後清除
        mBuilder.setAutoCancel(true);

        //显示指定文本

        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        // 提醒時間，單位是毫秒 (1/1000s)
        mBuilder.setWhen(System.currentTimeMillis());             // 設定為當下立刻啟動
//        mBuilder.setWhen(System.currentTimeMillis() - 3600000);   // 設定為系统時間少一小時 (會立刻叫)
//        mBuilder.setWhen(System.currentTimeMillis() + 3000 );        // 設定為 3 秒後

        // setOngoing(true) 設定為正在進行的通知，用戶無法清除 (類似 Foreground Service 通知)
        mBuilder.setOngoing(false);

        // 設定提醒方式
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);   // 震动提醒
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);     // 聲音
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);    // 三色燈
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);       // 以上三種方式一起

        // 設置震動方式，延遲零秒，震動一秒，延遲一秒、震動一秒
        mBuilder.setVibrate(new long[]{0, 1000, 1000, 1000});

        //mBuilder.setContentInfo("Info");
        //与setContentInfo类似，但如果设置了 setContentInfo 则无效果
        //用于当显示了多个相同ID的Notification时，显示消息总数
        //mBuilder.setNumber(2);
        //通知在状态栏显示时的文本
        //mBuilder.setTicker("在状态栏上显示的文本");
        //设置优先级


        // 5. 自訂畫面
        // Daily notification
        RemoteViews contentView = new RemoteViews(this.getPackageName(), R.layout.notification_daily);
//        contentView.setImageViewResource(R.id.imageview_notificaiton_task_icon_parent, R.drawable.icon_computer);
//        contentView.setTextViewText(R.id.textview_daily_summary_title, "Daily summary");

        RemoteViews remoteViewsItem;
        // 在 notification 中新增每一個 Task Items
        for(int i = 0 ; i < bean.size() ; ++i) {

            // [TODO] 可以設計一下只有計畫卻沒有結果的項目該怎麼顯示，目前先跳過這種項目
            // 如果沒有 cost_time，表示有計劃 (target) 但完全沒有相關紀錄 (record)
            if (bean.get(i).getCostTime() == -1) {
                continue;
            }

            remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);   // bind the view

            // (1) set icon
            remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, TimeManagementApplication.getIconResourceId(bean.get(i).getTaskIcon()));
            remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor(bean.get(i).getTaskColor()));

            // 檢查是否已經完成 (花費時間 >= 計畫時間，就代表已經花足夠時間了，可以把此 target 劃掉)
            if ( (bean.get(i).getCostTime() - bean.get(i).getPlanTime()) >= 0 ) {

                // (2) set task name
                remoteViewsItem.setTextViewText(R.id.textview_notification_task_name_complete, bean.get(i).getTaskName());

                // (3) set cost time
                remoteViewsItem.setTextViewText(R.id.textview_notification_cost_time_complete, ParseTime.intToHrM(bean.get(i).getCostTime()));

                // 完成項目的不同顏色和刪除線等等直接用另一個 view 畫出來
                remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setVisibility", View.VISIBLE);
                remoteViewsItem.setInt(R.id.textview_notification_cost_time_complete, "setVisibility", View.VISIBLE);
                remoteViewsItem.setInt(R.id.textview_notification_task_name, "setVisibility", View.GONE);
                remoteViewsItem.setInt(R.id.textview_notification_cost_time, "setVisibility", View.GONE);

                // 刪除線
                remoteViewsItem.setInt(R.id.image_notification_strikethrough_complete, "setVisibility", View.VISIBLE);

            } else { // 否則就要把此紀錄顯示為原本的顏色

                // (2) set task name & time
                remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, bean.get(i).getTaskName());

                // (3) set cost time
                remoteViewsItem.setTextViewText(R.id.textview_notification_cost_time, ParseTime.intToHrM(bean.get(i).getCostTime()));
            }

            // (2.1) set textView color
//        remoteViewsItem.setTextColor(R.id.textview_notification_task_name_complete, Color.parseColor(bean.get(i).getTaskColor()));

            // (2.2) set textView strikethrough (刪除線)
//        remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);


            if (bean.get(i).getMode().equals(Constants.MODE_DAILY))
            {
                // Daily
                Logger.d(Constants.TAG, MSG + "Daily: " + bean.get(i).getTaskName());
                contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);

            } else if (bean.get(i).getMode().equals(Constants.MODE_WEEKLY)) {

                // Weekly
                Logger.d(Constants.TAG, MSG + "Weekly: " + bean.get(i).getTaskName());
                contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);

            } else {
                Logger.d(Constants.TAG, MSG + "ELSE: " + bean.get(i).getTaskName());
            }
        }


        mBuilder.setContent(contentView);               // 如果不用 Builder 可以寫 notification.contentView = contentView;
        mBuilder.setCustomBigContentView(contentView);  // 可以設定通知縮起來的 layout（setContent) 和通知展開的 layout (setCustomBigContentView) (optional)
        mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());   // 加上這句，通知會有系統預設的框架。如果想要完全自訂就把這句拿掉
        mBuilder.setStyle(new android.support.v4.app.NotificationCompat.DecoratedCustomViewStyle());

        Intent intent = new Intent(this, MainActivity.class);

        // PendingIntent 作法 1
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pIntent);

        // PendingIntent 作法 2
//// Creates an explicit intent for an Activity in your app
//                Intent resultIntent = new Intent(this, MainActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//// Adds the back stack for the Intent (but not the Intent itself)
//                stackBuilder.addParentStack(ResultActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//                stackBuilder.addNextIntent(resultIntent);
//
//                mBuilder.setContentIntent(resultPendingIntent);
//                PendingIntent resultPendingIntent =
//                        stackBuilder.getPendingIntent(
//                                0,
//                                PendingIntent.FLAG_UPDATE_CURRENT
//                        );


        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);



        // Android 8.0 Oreo (APK 26) 以上必須設置通知頻道 Notification channels
        NotificationChannel channel;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID_SUMMARY,             // 此 ID 用來分辨不同的通知頻道
                    Constants.NOTIFICATION_CHANNEL_NAME_SUMMARY,
                    NotificationManager.IMPORTANCE_HIGH);   // 設為 IMPORTANCE_HIGH 以上才會在上方懸掛跳出來

            mNotificationManager.createNotificationChannel(channel);    // 使用 NotificationManager 加入這個頻道

            mBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID_SUMMARY);  // 呼叫 setChannelId 通知這個 Notification 的所屬頻道 ID

        } else {

            mBuilder.setDefaults(Notification.DEFAULT_ALL)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        mNotificationManager.notify(Constants.NOTIFY_ID_SUMMARY, mBuilder.build());   // 用 notify 並指定 ID，隨後可用此 ID 做進一步的更新或是取消等等操作
    }


    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

}