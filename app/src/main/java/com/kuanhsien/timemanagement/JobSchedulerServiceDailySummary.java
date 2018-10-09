package com.kuanhsien.timemanagement;

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
import android.widget.Toast;

import com.kuanhsien.timemanagement.dml.GetTraceDailySummaryAsyncTask;
import com.kuanhsien.timemanagement.dml.GetTraceDailySummaryCallback;
import com.kuanhsien.timemanagement.dml.GetTraceDetail;
import com.kuanhsien.timemanagement.dml.GetTraceDetailAsyncTask;
import com.kuanhsien.timemanagement.dml.GetTraceDetailCallback;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;
import com.kuanhsien.timemanagement.utils.ParseTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


// JobService 在 main thread 中執行，所有耗时邏輯應寫在背景執行緒中
public class JobSchedulerServiceDailySummary extends JobService {

    private static final String MSG = "JobSchedulerServiceDailySummary: ";


    public JobSchedulerServiceDailySummary() {
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
        Toast.makeText(this, "onStartJob", Toast.LENGTH_LONG).show();

        // （Method-1) 昨天日期
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, -1);
//        Date date = calendar.getTime();
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String lastVerNo = simpleDateFormat.format(date);

        // （Method-2) 昨天日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        String endVerNo = simpleDateFormat.format(date);

        // 上週日期
        date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7);
        String beginVerNo = simpleDateFormat.format(date);


        getAndNotifyTraceResults(params, "ALL", beginVerNo, endVerNo, "ALL", "ALL");

        return true;
    }

    // 有且僅有 onStartJob 返回值為 true 時，才會調用 onStopJob 銷毀 job
    // 返回 false 來直接銷毀這個工作
    @Override
    public boolean onStopJob(JobParameters params) {
        Logger.i(Constants.TAG, MSG + "onStopJob");
        return false;
    }

    private void startJobScheduler(long longNextTime) {

        Logger.d(Constants.TAG, MSG + "Start scheduling job");

        ComponentName componentName = new ComponentName(this, JobSchedulerServiceDailySummary.class.getName());   // service name

        JobInfo jobInfo = new JobInfo.Builder(Constants.SCHEDULE_JOB_ID_DAILY_SUMMARY, componentName)
//                .setPeriodic(10 * 1000)

                .setMinimumLatency(longNextTime)
                .setOverrideDeadline(longNextTime)
                .setPersisted(true)     // 為了讓重開機還能繼續執行此 job
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) // 只有網路不限流量時 (e.g. WIFI)
//                .setRequiresDeviceIdle(false)
//                .setRequiresCharging(false)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        int result = scheduler.schedule(jobInfo);   // start a jobScheduler task, return successful job id (return 0 if failed)

        if (result == JobScheduler.RESULT_SUCCESS) {
            Logger.d(Constants.TAG, MSG + "Job scheduled successfully!");
        }
    }



    public void getAndNotifyTraceResults(final JobParameters params, String mode, String startVerNo, String endVerNo, String categoryList, String taskList) {

        // insert time_tracing_table
        new GetTraceDailySummaryAsyncTask(mode, startVerNo, endVerNo, categoryList, taskList, new GetTraceDailySummaryCallback() {

            @Override
            public void onCompleted(List<GetTraceDetail> bean) {

                Logger.d(Constants.TAG, MSG + "GetTraceDailySummary onCompleted");
                for( int i = 0 ; i < bean.size() ; ++i) {
                    bean.get(i).LogD();
                }

                // (1) Notifcaiton title/ subtext/ content
                String strTitle = ""; //"Save " + ParseTime.msToHourMinDiff(recordList.get(0).getStartTime(), recordList.get(0).getEndTime()) + " to " + recordList.get(0).getTaskName();
                String strSubtext = "Record Summary"; //"Current task: " + recordList.get(recordList.size()-1).getTaskName(); // last element would be the current tracing item
                String strContent = ""; //"Today's total: ";

                Logger.d(Constants.TAG, MSG + "Title: " + strTitle);
                Logger.d(Constants.TAG, MSG + "Subtext: " + strSubtext);
                Logger.d(Constants.TAG, MSG + "Content: " + strContent);

                startNotification(bean, strTitle, strSubtext, strContent);    // use remoteViews to customized title and content


                // (2) 算出下次通知時間並註冊 Job
                startJobScheduler(ParseTime.getNextDailyNotifyMills("08:00:00"));

                // (3) 宣布當前的 job 已完成
                jobFinished(params,false);

            }

            @Override
            public void onError(String errorMessage) {

                Logger.d(Constants.TAG, MSG + "GetTraceDailySummary onError, errorMessage: " + errorMessage);
            }
        }).execute();
    }

    // 立即發送一個 notification
    private void startNotification(List<GetTraceDetail> bean, String strTitle, String strSubText, String strContent) {

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
        //与setContentInfo类似，但如果设置了setContentInfo则无效果
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

            remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);   // bind the view

            // (1) set icon
            remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, TimeManagementApplication.getIconResourceId(bean.get(i).getTaskIcon()));
            remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor(bean.get(i).getTaskColor()));

            // (2) set task name
            remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, bean.get(i).getTaskName());

            // (2.1) set textView color
//        remoteViewsItem.setTextColor(R.id.textview_notification_task_name_complete, Color.parseColor(bean.get(i).getTaskColor()));

            // (2.2) set textView strikethrough (刪除線)
//        remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);

            // (3) set cost time
            remoteViewsItem.setTextViewText(R.id.textview_notification_cost_time, ParseTime.intToHrM(bean.get(i).getCostTime()));



            // [TODO] 多撈出同一個時段的 PLAN 才能知道是否有完成 (FULL OUTER JOIN)
            // 完成項目的不同顏色和刪除線等等直接用另一個 view 畫出來
            // 完成的話要改下方寫法
//            // (2) set task name
//            remoteViewsItem.setTextViewText(R.id.textview_notification_task_name_complete, bean.get(i).getTaskName());
//            // (3) set cost time
//            remoteViewsItem.setTextViewText(R.id.textview_notification_cost_time_complete, ParseTime.intToHrM(bean.get(i).getCostTime()));
//
//            remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setVisibility", View.VISIBLE);
//            remoteViewsItem.setInt(R.id.textview_notification_cost_time_complete, "setVisibility", View.VISIBLE);
//            remoteViewsItem.setInt(R.id.textview_notification_task_name, "setVisibility", View.GONE);
//            remoteViewsItem.setInt(R.id.textview_notification_cost_time, "setVisibility", View.GONE);
//
//            remoteViewsItem.setInt(R.id.image_notification_strikethrough_complete, "setVisibility", View.VISIBLE);



            if (bean.get(i).getMode().equals(Constants.TAB_DAILY))
            {
                // Daily
                Logger.d(Constants.TAG, MSG + "Daily: " + bean.get(i).getTaskName());
                contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);

            } else if (bean.get(i).getMode().equals(Constants.TAB_WEEKLY)) {

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

}
