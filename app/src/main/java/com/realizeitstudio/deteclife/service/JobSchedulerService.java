package com.realizeitstudio.deteclife.service;

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

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;


// JobService 在 main thread 中執行，所有耗时邏輯應寫在背景執行緒中
public class JobSchedulerService extends JobService {

    private static final String MSG = "JobSchedulerService: ";


    public JobSchedulerService() {
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
//        Toast.makeText(this, "onStartJob", Toast.LENGTH_LONG).show();

        startNotification();
        
        jobFinished(params,false);

        startJobScheduler();
        return true;
    }


    // 有且僅有 onStartJob 返回值為 true 時，才會調用 onStopJob 銷毀 job
    // 返回 false 來直接銷毀這個工作
    @Override
    public boolean onStopJob(JobParameters params) {
        Logger.i(Constants.TAG, MSG + "onStopJob");
        return false;
    }


    private void startJobScheduler() {

        Logger.d(Constants.TAG, MSG + "Start scheduling job");

        ComponentName componentName = new ComponentName(this, JobSchedulerService.class.getName());   // service name

        JobInfo jobInfo = new JobInfo.Builder(Constants.SCHEDULE_JOB_ID_DAILY_SUMMARY, componentName)
//                .setPeriodic(10 * 1000)

                .setMinimumLatency(10*1000)
                .setOverrideDeadline(10*1000)
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






    // 立即發送一個 notification
    private void startNotification() {
        int id = 9487;  // 設一個自己知道的 ID 就可以
        String TEST_NOTIFY_ID = "test"; // 通知頻道的 ID

        // Large Icon 作法 1
        Drawable drawable = ContextCompat.getDrawable(this, TimeManagementApplication.getIconResourceId(Constants.APP_ICON_BIG));
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Large Icon 作法 2
//                Bitmap largeIcon = BitmapFactory.decodeResource(
//                        getResources(), R.drawable.icon_sleep);

//                // 建立通知物件 作法 1
//                Notification notification = builder.build();
//                // 使用BASIC_ID為編號發出通知
//                manager.notify(BASIC_ID, notification);

        // 建立通知物件 作法 2: 透過 builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, TEST_NOTIFY_ID);

        //设置小图标
        mBuilder.setSmallIcon(R.drawable.btn_like_selected);
        //设置大图标
        mBuilder.setLargeIcon(bitmap);
        //设置标题
        mBuilder.setContentTitle("这是标题");
        //设置通知正文
        mBuilder.setContentText("这是正文，当前ID是：" + id);
        //设置摘要
        mBuilder.setSubText("这是摘要");
        //设置是否点击消息后自动clean
        mBuilder.setAutoCancel(true);
        //显示指定文本
        mBuilder.setContentInfo("Info");
        //与setContentInfo类似，但如果设置了setContentInfo则无效果
        //用于当显示了多个相同ID的Notification时，显示消息总数
        mBuilder.setNumber(2);
        //通知在状态栏显示时的文本
        mBuilder.setTicker("在状态栏上显示的文本");
        //设置优先级
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        // 提醒時間，單位是毫秒 (1/1000s)
//                mBuilder.setWhen(System.currentTimeMillis() - 3600000);   // 設定為系统時間少一小時 (會立刻叫)
//                mBuilder.setWhen(System.currentTimeMillis());             // 設定為當下立刻啟動
        mBuilder.setWhen(System.currentTimeMillis() + 3000 );        // 設定為 3 秒後

        // setOngoing(true) 設定為正在進行的通知，用戶無法清除 (類似 Foreground Service 通知)
//                mBuilder.setOngoing(true);
        mBuilder.setOngoing(false);

        // 設定提醒方式
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);   // 震动提醒
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);     // 聲音
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);    // 三色燈
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);       // 以上三種方式一起

        //设置震动方式，延迟零秒，震动一秒，延迟一秒、震动一秒
        mBuilder.setVibrate(new long[]{0, 1000, 1000, 1000});

        // 上面一項一項分開設定的簡化版，可以全部連在一起
//                // 設定小圖示、大圖示、狀態列文字、時間、內容標題、內容訊息和內容額外資訊
//                builder.setSmallIcon(R.drawable.icon_book)
//                        .setWhen(System.currentTimeMillis())
//                        .setContentTitle("Basic Notification")
//                        .setContentText("Demo for basic notification control.")
//                        .setContentInfo("3");

//                // 2. 多文本通知
//                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
//                style.bigText("这里可以写很长的一段话，不信你试试。这里可以写很长的一段话，不信你试试。这里可以写很长的一段话，不信你试试。");
//                style.setBigContentTitle("点击后的标题");
//                style.setSummaryText("这是摘要");
//                mBuilder.setStyle(style);
//
//                // 3. 擴展佈局 (可放列表) 應用於通知 (和 2. 多文本通知 擇一)
//                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//                String[] events = {"第一", "第二", "第三", "第四", "第五", "第六", "第七"};
//                inboxStyle.setBigContentTitle("展开后的标题");
//                inboxStyle.setSummaryText("这是摘要");
//                for (String event : events) {
//                    inboxStyle.addLine(event);
//                }
//                mBuilder.setStyle(inboxStyle);
//
//                // 4. 大圖通知
//                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
//                bigPictureStyle.setBigContentTitle("展开后的标题");
//                bigPictureStyle.setSummaryText("这是摘要");
//                bigPictureStyle.bigPicture(bitmap);
//                mBuilder.setStyle(bigPictureStyle);

        // 5. 自訂畫面
        // Daily notification
        RemoteViews contentView = new RemoteViews(this.getPackageName(), R.layout.notification_daily);
//        contentView.setImageViewResource(R.id.imageview_notificaiton_task_icon_parent, R.drawable.icon_computer);
//        contentView.setTextViewText(R.id.textview_daily_summary_title, "Daily summary");

        // 在 notification 中新增每一個 Task Items
        RemoteViews remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_book);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Book");



        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);




        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);


        // (1) set icon
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_sleep);
        remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor("#F4A460"));

        // (2) set task name
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name_complete, "Book");

        // (2.1) set textView color
//        remoteViewsItem.setTextColor(R.id.textview_notification_task_name_complete, Color.parseColor("#AAAAAA"));

        // (2.2) set textView strikethrough (刪除線)
//        remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);

        // 最後決定完成項目的 style 直接用另一個 view 畫出來
        remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setVisibility", View.VISIBLE);
        remoteViewsItem.setInt(R.id.textview_notification_cost_time_complete, "setVisibility", View.VISIBLE);
        remoteViewsItem.setInt(R.id.textview_notification_task_name, "setVisibility", View.GONE);
        remoteViewsItem.setInt(R.id.textview_notification_cost_time, "setVisibility", View.GONE);

        remoteViewsItem.setInt(R.id.image_notification_strikethrough_complete, "setVisibility", View.VISIBLE);



        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);





        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);

//        gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.selector_notification_icon);
//        gradientDrawable.setColor(Color.parseColor("#393920"));


        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_work);
        remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor("#32CD32"));
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Work");

        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);

        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor("#C71585"));
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_walk);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Walk");

        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);

        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor("#1E90FF"));
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_swim);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Swim");

        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);

        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_drunk);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Drunk");

        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);





//Weekly


        // 在 notification 中新增每一個 Task Items
        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_book);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Bookabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");


        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);

        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_sleep);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Sleep");

        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);

        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_work);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Work");

        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);

        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_walk);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Walk");

        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);

        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_swim);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Swim");

        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);

        remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);
        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_drunk);
        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Drunk");

//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);




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
                    TEST_NOTIFY_ID,             // 此 ID 用來分辨不同的通知頻道
                    "Notify Test",
                    NotificationManager.IMPORTANCE_HIGH);   // 設為 IMPORTANCE_HIGH 以上才會在上方懸掛跳出來

            mNotificationManager.createNotificationChannel(channel);    // 使用 NotificationManager 加入這個頻道

            mBuilder.setChannelId(TEST_NOTIFY_ID);  // 呼叫 setChannelId 通知這個 Notification 的所屬頻道 ID

        } else {

            mBuilder.setDefaults(Notification.DEFAULT_ALL)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        mNotificationManager.notify(id,mBuilder.build());   // 用 notify 並指定 ID，隨後可用此 ID 做進一步的更新或是取消等等操作
    }

}
