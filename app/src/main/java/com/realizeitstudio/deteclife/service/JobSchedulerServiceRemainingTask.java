package com.realizeitstudio.deteclife.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCurrentTraceTaskAsyncTask;
import com.realizeitstudio.deteclife.dml.GetCurrentTraceTaskCallback;
import com.realizeitstudio.deteclife.dml.GetResultDailySummary;
import com.realizeitstudio.deteclife.dml.GetResultDailySummaryAsyncTask;
import com.realizeitstudio.deteclife.dml.GetResultDailySummaryCallback;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ken on 2018/10/25
 *
 * JobService 在 main thread 中執行，所有耗时邏輯應寫在背景執行緒中
 */
public class JobSchedulerServiceRemainingTask extends JobService {

    private static final String MSG = "JobSchedulerServiceRemainingTask: ";
    private JobParameters mJobParameters;

    private boolean mLoading = false;
    private boolean mLoadingCurrentTracing = false;

    private TimeTracingTable mCurItem;


    public JobSchedulerServiceRemainingTask() {
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

        mJobParameters = params;

        // 定義時間格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
        // 透過SimpleDateFormat的format方法將 Date 轉為字串
        String strVerNo = simpleDateFormat.format(new Date());

        // [Query 1] current complete item
        getCurrentTraceItem(strVerNo);

        return true;
    }

    // 有且僅有 onStartJob 返回值為 true 時，才會調用 onStopJob 銷毀 job
    // 返回 false 來直接銷毀這個工作
    @Override
    public boolean onStopJob(JobParameters params) {
        Logger.i(Constants.TAG, MSG + "onStopJob");
        return false;
    }

    // [Query 1] current complete item
    // 因為 app 可能是關閉的， notification 之前還是需要重撈一次想要顯示的內容
    // 如果有 plantime, 且今天能完成，才會有這個 job
    public void getCurrentTraceItem(String strVerNo) {

        // [TODO] no need loading flag
        if (!isLoadingCurrentTracing()) {
            setLoadingCurrentTracing(true);

            new GetCurrentTraceTaskAsyncTask(strVerNo, new GetCurrentTraceTaskCallback() {

                @Override
                public void onCompleted(TimeTracingTable bean) {

                    Logger.d(Constants.TAG, MSG + "GetCurrentTraceTaskAsyncTask onCompleted");
                    bean.logD();

                    // get current item
                    mCurItem = bean;
                    setLoadingCurrentTracing(false);

                    // 今天日期
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
                    Date date = new Date();
                    String endVerNo = simpleDateFormat.format(date);

                    // 計算 Weekly 的開始時間 (beginVerNo)
                    int intWeekDay = ParseTime.date2Day(date);    // 把今天傳入，回傳星期幾 (1 = 星期一，2 = 星期二)
                    date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (intWeekDay - 1)); // 如果今天是星期一，則 Weekly 也只撈一天 (beginVerNo = endVerNo)，和 endVerNo 一樣只往回減一天
                    String beginVerNo = simpleDateFormat.format(date);

                    // [Query 2] current remaining items
                    getAndNotifyTraceResults("ALL", beginVerNo, endVerNo, "ALL", "ALL");
                }

                @Override
                public void onError(String errorMessage) {
                    setLoadingCurrentTracing(false);
                    Logger.e(Constants.TAG, MSG + "GetGetCurrentTraceTask.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }


    // [Query 2] current remaining items
    public void getAndNotifyTraceResults(String mode, String startVerNo, String endVerNo, String categoryList, String taskList) {

        if (!isLoading()) {
            setLoading(true);

            // get daily summary of tracing and planning result
            new GetResultDailySummaryAsyncTask(mode, startVerNo, endVerNo, categoryList, taskList, new GetResultDailySummaryCallback() {

                @Override
                public void onCompleted(List<GetResultDailySummary> bean) {

                    Logger.d(Constants.TAG, MSG + "GetResultDailySummary onCompleted: bean.size() = " + bean.size());
                    for (int i = 0; i < bean.size(); ++i) {
                        bean.get(i).logD();
                    }


                    // 先簡單處理資料，找出今天的 plan 項目
                    // 只有有 plan 的項目放在 notification 上
                    // 這段的寫法可以參考 recordPresenter 的 getResultDailySummary
                    List<GetResultDailySummary> dailySummaryList = new ArrayList<>();
                    List<GetResultDailySummary> weeklySummaryList = new ArrayList<>();

//                    long longCompleteTaskCostTimeWeekly = 0;
                    long longRemainingTaskCostTimeDaily = 0;
                    long longRemainingTaskCostTimeWeekly = 0;

                    for (int i = 0; i < bean.size(); ++i) {
                        bean.get(i).logD();

                        // 分別存成 daily 和 weekly 的結果，TODO 放進兩個不同的 adapter 中，甚至一次撈一整週
                        if (Constants.MODE_DAILY.equals(bean.get(i).getMode())) {     // Daily summary

                            Logger.d(Constants.TAG, MSG + "MODE_DAILY => ");
                            dailySummaryList.add(bean.get(i));

                            // 如果遇到現在正在進行的項目，抓他的 plan_time - cost_time
                            if (bean.get(i).getTaskName().equals(mCurItem.getTaskName())) {

//                                Logger.d(Constants.TAG, MSG + "current item, remaining: " + (bean.get(i).getPlanTime() - bean.get(i).getCostTime()));

                            } else if (bean.get(i).getPlanTime() - bean.get(i).getCostTime() > 0) {   // 其他 task 還沒做完的話，把剩餘的 plan time 加總

                                longRemainingTaskCostTimeDaily += bean.get(i).getPlanTime() - bean.get(i).getCostTime();
                                Logger.d(Constants.TAG, MSG + "remaining item => remaining: " + (bean.get(i).getPlanTime() - bean.get(i).getCostTime()) + " => after: " + ParseTime.msToHourMin(bean.get(i).getPlanTime() - bean.get(i).getCostTime()));
                                Logger.d(Constants.TAG, MSG + "total remaining time: " + longRemainingTaskCostTimeDaily + " => after: " + ParseTime.msToHourMin(longRemainingTaskCostTimeDaily));
                            }

                        } else {    // Constants.MODE_WEEKLY.equals(bean.get(i).getMode())  // Weekly summary

                            Logger.d(Constants.TAG, MSG + "MODE_WEEKLY => ");
                            weeklySummaryList.add(bean.get(i));

//                            // 如果遇到現在正在進行的項目，抓他的 plan_time - cost_time
//                            if (bean.get(i).getTaskName().equals(mCurItem.getTaskName())) {
//
//                                longCompleteTaskCostTimeWeekly = bean.get(i).getPlanTime() - bean.get(i).getCostTime();
//                                Logger.d(Constants.TAG, MSG + "current item => complete after (ms): " + longCompleteTaskCostTimeWeekly + " => after : " + ParseTime.msToHourMin(longCompleteTaskCostTimeWeekly));
//
//                            } else
                            if (bean.get(i).getPlanTime() - bean.get(i).getCostTime() > 0) {    // 其他 task 還沒做完的話，把剩餘的 plan time 加總

                                longRemainingTaskCostTimeWeekly += bean.get(i).getPlanTime() - bean.get(i).getCostTime();
                                Logger.d(Constants.TAG, MSG + "remaining item => remaining: " + (bean.get(i).getPlanTime() - bean.get(i).getCostTime()) + " => after: " + ParseTime.msToHourMin(bean.get(i).getPlanTime() - bean.get(i).getCostTime()));
                                Logger.d(Constants.TAG, MSG + "total remaining time: " + longRemainingTaskCostTimeWeekly + " => after: " + ParseTime.msToHourMin(longRemainingTaskCostTimeWeekly));
                            }
                        }
                    }

                    // (1) Notifcaiton title/ subtext/ content
                    // strSubtext 會顯示在 notification 的頂端，strTitle 和 strContent 反而都沒有顯示
                    String strTitle = ""; //"Save " + ParseTime.msToHourMinDiff(recordList.get(0).getStartTime(), recordList.get(0).getEndTime()) + " to " + recordList.get(0).getTaskName();
                    String strSubtext = "Remaining Tasks "; //"Current task: " + recordList.get(recordList.size()-1).getTaskName(); // last element would be the current tracing item
                    String strContent = "Would you like to start another task?"; //"Today's total: ";

                    Logger.d(Constants.TAG, MSG + "Title: " + strTitle);
                    Logger.d(Constants.TAG, MSG + "Subtext: " + strSubtext);
                    Logger.d(Constants.TAG, MSG + "Content: " + strContent);

                    // [TODO] 目前只提醒 daily plan 的剩餘項目，也許可以考慮也提醒 weekly 的剩餘項目，或是當前 task 的剩餘時間
                    startNotification(dailySummaryList, strTitle, strSubtext, strContent);    // use remoteViews to customized title and content

                    setLoading(false);

                    // (2) 這是一次性的 job，不用註冊下一次的 job。宣布當前的 job 已完成
                    jobFinished(mJobParameters,false);
                }

                @Override
                public void onError(String errorMessage) {

                    setLoading(false);
                    Logger.d(Constants.TAG, MSG + "GetResultDailySummary onError, errorMessage: " + errorMessage);

                }

            }).execute();
        }
    }


    // 立即發送一個 notification
    private void startNotification(List<GetResultDailySummary> bean, String strTitle, String strSubText, String strContent) {

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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_COMPLETE);

        // 設置小圖標
        notificationBuilder.setSmallIcon(TimeManagementApplication.getIconResourceId(Constants.APP_ICON_SMALL));
        // 設置大圖標
        notificationBuilder.setLargeIcon(bitmap);

        // 標題
        notificationBuilder.setContentTitle(strTitle);
        // 內容
        notificationBuilder.setContentText(strContent);
        // 摘要
        notificationBuilder.setSubText(strSubText);

        // 點擊後清除
        notificationBuilder.setAutoCancel(true);
        //显示指定文本

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        // 提醒時間，單位是毫秒 (1/1000s)
        notificationBuilder.setWhen(System.currentTimeMillis());             // 設定為當下立刻啟動
//        mBuilder.setWhen(System.currentTimeMillis() - 3600000);   // 設定為系统時間少一小時 (會立刻叫)
//        mBuilder.setWhen(System.currentTimeMillis() + 3000 );        // 設定為 3 秒後

        // setOngoing(true) 設定為正在進行的通知，用戶無法清除 (類似 Foreground Service 通知)
        notificationBuilder.setOngoing(false);

        // 設定提醒方式
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);   // 震动提醒
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);     // 聲音
//                mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);    // 三色燈
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);       // 以上三種方式一起

        // 設置震動方式，延遲零秒，震動一秒，延遲一秒、震動一秒
        notificationBuilder.setVibrate(new long[]{0, 1000, 1000, 1000});

        //mBuilder.setContentInfo("Info");
        //与setContentInfo类似，但如果设置了setContentInfo则无效果
        //用于当显示了多个相同ID的Notification时，显示消息总数
        //mBuilder.setNumber(2);
        //通知在状态栏显示时的文本
        //mBuilder.setTicker("在状态栏上显示的文本");
        //设置优先级


        // 5. 自訂畫面
        // Daily notification
        RemoteViews contentView = new RemoteViews(this.getPackageName(), R.layout.notification_task_complete);
//        contentView.setImageViewResource(R.id.imageview_notificaiton_task_icon_parent, R.drawable.icon_computer);
        contentView.setTextViewText(R.id.textview_daily_complete_task_content, strContent);

        RemoteViews remoteViewsItem;
        // 在 notification 中新增每一個 Task Items
        for (int i = 0; i < bean.size(); ++i) {

            // [TODO] 這裡的提醒主要做的是目標的完成度，所以只顯示有計劃的項目，目前先跳過只有 record 的項目
            // 如果沒有 cost_time，表示有計劃 (target) 但完全沒有相關紀錄 (record)
            if (bean.get(i).getPlanTime() < 0) {
                continue;
            }

            remoteViewsItem = new RemoteViews(this.getPackageName(), R.layout.notification_item);   // bind the view

            // (1) set icon
            remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, TimeManagementApplication.getIconResourceId(bean.get(i).getTaskIcon()));
            remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor(bean.get(i).getTaskColor()));

            // 檢查是否已經完成 (本來有計劃，且花費時間 >= 計畫時間，就代表已經花足夠時間了，可以把此 target 劃掉)
            if ((bean.get(i).getCostTime() - bean.get(i).getPlanTime()) >= 0) {

                // (2) set task name
                remoteViewsItem.setTextViewText(R.id.textview_notification_task_name_complete, bean.get(i).getTaskName());

                // (3) set cost time
                remoteViewsItem.setTextViewText(R.id.textview_notification_cost_time_complete, ParseTime.msToHrM(bean.get(i).getPlanTime()));

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
                remoteViewsItem.setTextViewText(R.id.textview_notification_cost_time, ParseTime.msToHrM(bean.get(i).getPlanTime()));
            }

            // (2.1) set textView color
//        remoteViewsItem.setTextColor(R.id.textview_notification_task_name_complete, Color.parseColor(bean.get(i).getTaskColor()));

            // (2.2) set textView strikethrough (刪除線)
//        remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);


            if (bean.get(i).getMode().equals(Constants.MODE_DAILY)) {
                // Daily
                Logger.d(Constants.TAG, MSG + "Daily: " + bean.get(i).getTaskName());
                contentView.addView(R.id.linearlayout_notification_daily_complete, remoteViewsItem);

//            } else if (bean.get(i).getMode().equals(Constants.MODE_WEEKLY)) {
//
//                // Weekly
//                Logger.d(Constants.TAG, MSG + "Weekly: " + bean.get(i).getTaskName());
//                contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//
//            }
            } else {
                Logger.d(Constants.TAG, MSG + "ELSE: " + bean.get(i).getTaskName());
            }
        }


        notificationBuilder.setContent(contentView);               // 如果不用 Builder 可以寫 notification.contentView = contentView;
        notificationBuilder.setCustomBigContentView(contentView);  // 可以設定通知縮起來的 layout（setContent) 和通知展開的 layout (setCustomBigContentView) (optional)
        notificationBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());   // 加上這句，通知會有系統預設的框架。如果想要完全自訂就把這句拿掉
        notificationBuilder.setStyle(new android.support.v4.app.NotificationCompat.DecoratedCustomViewStyle());

        Intent intent = new Intent(this, MainActivity.class);

        // PendingIntent 作法 1
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);

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


        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);



        // Android 8.0 Oreo (APK 26) 以上必須設置通知頻道 Notification channels
        NotificationChannel channel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID_COMPLETE,             // 此 ID 用來分辨不同的通知頻道
                    Constants.NOTIFICATION_CHANNEL_NAME_COMPLETE,
                    NotificationManager.IMPORTANCE_HIGH);   // 設為 IMPORTANCE_HIGH 以上才會在上方懸掛跳出來

            notificationManager.createNotificationChannel(channel);    // 使用 NotificationManager 加入這個頻道

            notificationBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID_COMPLETE);  // 呼叫 setChannelId 通知這個 Notification 的所屬頻道 ID

        } else {

            notificationBuilder.setDefaults(Notification.DEFAULT_ALL)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        notificationManager.notify(Constants.NOTIFY_ID_COMPLETE, notificationBuilder.build());   // 用 notify 並指定 ID，隨後可用此 ID 做進一步的更新或是取消等等操作
    }


    public boolean isLoading() {
        return mLoading;
    }

    public boolean isLoadingCurrentTracing() {
        return mLoadingCurrentTracing;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    public void setLoadingCurrentTracing(boolean loadingCurrentTracing) {
        mLoadingCurrentTracing = loadingCurrentTracing;
    }

}
