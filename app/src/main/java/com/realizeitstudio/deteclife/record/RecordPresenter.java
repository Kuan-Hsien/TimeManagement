package com.realizeitstudio.deteclife.record;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskListAsyncTask;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskListCallback;
import com.realizeitstudio.deteclife.dml.GetCurrentTraceTaskAsyncTask;
import com.realizeitstudio.deteclife.dml.GetCurrentTraceTaskCallback;
import com.realizeitstudio.deteclife.dml.GetResultDailySummary;
import com.realizeitstudio.deteclife.dml.GetResultDailySummaryAsyncTask;
import com.realizeitstudio.deteclife.dml.GetResultDailySummaryCallback;
import com.realizeitstudio.deteclife.dml.GetTraceSummary;
import com.realizeitstudio.deteclife.dml.SetRecordAsyncTask;
import com.realizeitstudio.deteclife.dml.SetRecordCallback;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.service.JobSchedulerServiceComplete;
import com.realizeitstudio.deteclife.service.JobSchedulerServiceRemainingTask;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/10/07.
 */
//public class RecordPresenter implements RecordContract.Presenter {

public class RecordPresenter implements RecordContract.Presenter {
    private static final String MSG = "RecordPresenter: ";

    private final RecordContract.View mRecordView;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;
    private boolean mLoadingCurrentTracing = false;
    private boolean mLoadingResultSummary = false;


    public RecordPresenter(RecordContract.View mainView) {
        mRecordView = checkNotNull(mainView, "mainView cannot be null!");
        mRecordView.setPresenter(this);
    }

    @Override
    public void start() {

        // (1) Task List
        getCategoryTaskList();

        // (2) Current Trace Task
        // 取得現在時間
        Date curDate = new Date();
        // 定義時間格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
        // 透過SimpleDateFormat的format方法將 Date 轉為字串
        String strVerNo = simpleDateFormat.format(curDate);

        getCurrentTraceItem(strVerNo);
    }


    // 0-1. recyclerView Scroll event
    @Override
    public void onScrollStateChanged(int visibleItemCount, int totalItemCount, int newState) {

        if (newState == RecyclerView.SCROLL_STATE_IDLE && visibleItemCount > 0) {

            if (mlastVisibleItemPosition == totalItemCount - 1) {
                Logger.d(Constants.TAG, MSG + "Scroll to bottom");

            } else if (mfirstVisibleItemPosition == 0) {

                // Scroll to top
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView.LayoutManager layoutManager) {

        if (layoutManager instanceof LinearLayoutManager) {

            mlastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
            mfirstVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findFirstVisibleItemPosition();

        } else if (layoutManager instanceof GridLayoutManager) {

            mlastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
            mfirstVisibleItemPosition = ((GridLayoutManager) layoutManager)
                    .findFirstVisibleItemPosition();
        }
    }

    // 0-2. [Send-to-View] request fragment to refresh adapter (base on mode (view or edit))
    @Override
    public void refreshUi(int mode) {
        mRecordView.refreshUi(mode);
    }


    // 1-1. [Send-to-Model] database query to prepare data (query all records)
    @Override
    public void getCategoryTaskList() {
        if (!isLoading()) {
            setLoading(true);

            new GetCategoryTaskListAsyncTask(new GetCategoryTaskListCallback() {

                @Override
                public void onCompleted(List<GetCategoryTaskList> bean) {
                    setLoading(false);

                    // create a new task list (without category items, only show tasks to choose on record page)
                    List<GetCategoryTaskList> taskLists = new ArrayList<>();

                    for (int i = 0; i < bean.size(); ++i) {
                        if (Constants.ITEM_TASK.equals(bean.get(i).getItemCatg())) {
                            taskLists.add(bean.get(i));
                        }
                    }

                    showCategoryTaskList(taskLists);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, "GetCategoryTaskList.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }


    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showCategoryTaskList(List<GetCategoryTaskList> bean) {
        mRecordView.showCategoryTaskList(bean);
    }



/////////////////////////////
    // 1-1. [Send-to-Model] database query to prepare data (query current record item)
    @Override
    public void getCurrentTraceItem(String strVerNo) {

        // [TODO] no need loading flag
        if (!isLoadingCurrentTracing()) {
            setLoadingCurrentTracing(true);

            new GetCurrentTraceTaskAsyncTask(strVerNo, new GetCurrentTraceTaskCallback() {

                @Override
                public void onCompleted(TimeTracingTable bean) {
                    setLoadingCurrentTracing(false);
                    showCurrentTraceItem(bean);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoadingCurrentTracing(false);
                    Logger.e(Constants.TAG, MSG + "GetGetCurrentTraceTask.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }


    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showCurrentTraceItem(TimeTracingTable bean) {
        mRecordView.showCurrentTraceItem(bean);
    }

    @Override
    public void saveTraceResults(final List<TimeTracingTable> recordList, String startVerNo, String endVerNo, String categoryList, String taskList) {

        // insert time_tracing_table
        new SetRecordAsyncTask(recordList, startVerNo, endVerNo, categoryList, taskList, new SetRecordCallback() {

            // after insert a new record, need to get the current trace summary and show in notification
            @Override
            public void onCompleted(List<GetTraceSummary> bean) {

                Logger.d(Constants.TAG, MSG + "SetRecord onCompleted");
                for (int i = 0; i < bean.size(); ++i) {
                    bean.get(i).logD();
                }
                // [TODO] 更新 Record Fragment 內容，getCategoryTaskList 和 getCurrentTraceItem 其實都可以不做，應該是進來這頁的時候重撈就可以。同時 getCurrentTraceItem 其實不用重撈，可以從傳入要 save 資料的 bean 去抓 currentTraceItem
                // (1) Task List
                getCategoryTaskList();


                // (2) Current Trace Task
                // 取得現在時間
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strVerNo = simpleDateFormat.format(curDate);

                getCurrentTraceItem(strVerNo);

                // [TODO] (3) Start notifications
                // (3-1) ongoing task
                // (3-2) book a complete message for current task (remaining time for current task) (one time job)
                // (3-3) book a remaining tasks notification (one time job)


                // (3-1) ongoing task
                // Notifcaiton title/ subtext/ content
                // [TODO] daily total
                String strTitle = "Save " + ParseTime.msToHourMinDiff(recordList.get(0).getStartTime(), recordList.get(0).getEndTime()) + " to " + recordList.get(0).getTaskName();
                String strSubtext = "Current task: " + recordList.get(recordList.size() - 1).getTaskName(); // last element would be the current tracing item
                String strContent = "Today's total: ";
                for (int i = 0; i < bean.size(); ++i) {
                    strContent += bean.get(i).getTaskName() + " " + ParseTime.msToHourMin(bean.get(i).getCostTime());
                }

                Logger.d(Constants.TAG, MSG + "Title: " + strTitle);
                Logger.d(Constants.TAG, MSG + "Subtext: " + strSubtext);
                Logger.d(Constants.TAG, MSG + "Content: " + strContent);

                startNotificationOngoing(strTitle, strSubtext, strContent);


                // (3-2) book a complete message for current task (remaining time for current task) (one time job)
                // (3.2.0) 撈當前這個 task plan time 多久，今天已經做了多久 (summary result)，並算出來多久以後要提醒完成
                // (3.2.1) 如果 task 在今天來不及做完，或是已經超過 plan_time 了，就不會起這個 job for notification
                // (3.2.2) 如果 task 來得及做完，就會起這個 job for notification (但不用 repeat)
                // (3-3) book a remaining tasks notification (one time job)
                // 傳入 current task
                getResultDailySummary(recordList.get(recordList.size() - 1));

                // [TODO] insert 資料後跳轉 Trace Fragemnt (該 Fragment 需要重新抓取資料)
                // (4) 從這裡回到 RecordFragment，回到 MainActivity > MainPresenter > TraceDailyFragment 更新
                mRecordView.showStatisticUi();
            }

            @Override
            public void onError(String errorMessage) {

                Logger.d(Constants.TAG, MSG + "SetRecord onError, errorMessage: " + errorMessage);

                // [TODO] insert 資料後跳轉 Trace Fragemnt (該 Fragment 需要重新抓取資料) (但是這筆資料就沒有順利存入)

//                refreshUi(Constants.MODE_PLAN_VIEW);
            }
        }).execute();
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    public boolean isLoadingCurrentTracing() {
        return mLoadingCurrentTracing;
    }

    public void setLoadingCurrentTracing(boolean loadingCurrentTracing) {
        mLoadingCurrentTracing = loadingCurrentTracing;
    }


    @Override
    public void showAddTaskUi() {
        mRecordView.showAddTaskUi();
    }


    public void getResultDailySummary(final TimeTracingTable curItem) {
        Logger.d(Constants.TAG, MSG + "getResultDailySummary: ");

        if (!isLoadingResultSummary()) {
            setLoadingResultSummary(true);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);

            // 今天日期
            Date date = new Date();
            String endVerNo = simpleDateFormat.format(date);

            int intWeekDay = ParseTime.date2Day(date);    // 把今天傳入，回傳星期幾 (1 = 星期一，2 = 星期二)

            // 計算 Weekly 的開始時間 (beginVerNo)
            date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (intWeekDay - 1)); // 如果今天是星期一，則 Weekly 也只撈一天 (beginVerNo = endVerNo)，和 endVerNo 一樣只往回減一天
            String beginVerNo = simpleDateFormat.format(date);


            // get daily summary of tracing and planning result
            new GetResultDailySummaryAsyncTask("ALL", beginVerNo, endVerNo, "ALL", "ALL", new GetResultDailySummaryCallback() {

                @Override
                public void onCompleted(List<GetResultDailySummary> bean) {

                    Logger.d(Constants.TAG, MSG + "GetResultDailySummary onCompleted: bean.size() = " + bean.size());

                    List<GetResultDailySummary> dailySummaryList = new ArrayList<>();
                    List<GetResultDailySummary> weeklySummaryList = new ArrayList<>();

                    long longCompleteTaskCostTimeDaily = 0;
                    long longCompleteTaskCostTimeWeekly = 0;
                    long longRemainingTaskCostTimeDaily = 0;
                    long longRemainingTaskCostTimeWeekly = 0;

                    for (int i = 0; i < bean.size(); ++i) {
                        bean.get(i).logD();

                        // 分別存成 daily 和 weekly 的結果，TODO 放進兩個不同的 adapter 中，甚至一次撈一整週
                        if (Constants.MODE_DAILY.equals(bean.get(i).getMode())) {     // Daily summary

                            Logger.d(Constants.TAG, MSG + "MODE_DAILY => ");
                            dailySummaryList.add(bean.get(i));

                            // 如果遇到現在正在進行的項目，抓他的 plan_time - cost_time
                            if (bean.get(i).getTaskName().equals(curItem.getTaskName())) {

                                longCompleteTaskCostTimeDaily = bean.get(i).getPlanTime() - bean.get(i).getCostTime();
                                Logger.d(Constants.TAG, MSG + "current item => complete after (ms): " + longCompleteTaskCostTimeDaily + " => after : " + ParseTime.msToHourMin(longCompleteTaskCostTimeDaily));

                            } else if (bean.get(i).getPlanTime() - bean.get(i).getCostTime() > 0) {   // 其他 task 還沒做完的話，把剩餘的 plan time 加總

                                longRemainingTaskCostTimeDaily += bean.get(i).getPlanTime() - bean.get(i).getCostTime();
                                Logger.d(Constants.TAG, MSG + "remaining item => remaining: " + (bean.get(i).getPlanTime() - bean.get(i).getCostTime()) + " => after: " + ParseTime.msToHourMin(bean.get(i).getPlanTime() - bean.get(i).getCostTime()));
                                Logger.d(Constants.TAG, MSG + "total remaining time: " + longRemainingTaskCostTimeDaily + " => after: " + ParseTime.msToHourMin(longRemainingTaskCostTimeDaily));
                            }

                        } else {    // Constants.MODE_WEEKLY.equals(bean.get(i).getMode())  // Weekly summary

                            Logger.d(Constants.TAG, MSG + "MODE_WEEKLY => ");
                            weeklySummaryList.add(bean.get(i));

                            // 如果遇到現在正在進行的項目，抓他的 plan_time - cost_time
                            if (bean.get(i).getTaskName().equals(curItem.getTaskName())) {

                                longCompleteTaskCostTimeWeekly = bean.get(i).getPlanTime() - bean.get(i).getCostTime();
                                Logger.d(Constants.TAG, MSG + "current item => complete after (ms): " + longCompleteTaskCostTimeWeekly + " => after : " + ParseTime.msToHourMin(longCompleteTaskCostTimeWeekly));

                            } else if (bean.get(i).getPlanTime() - bean.get(i).getCostTime() > 0) {    // 其他 task 還沒做完的話，把剩餘的 plan time 加總

                                longRemainingTaskCostTimeWeekly += bean.get(i).getPlanTime() - bean.get(i).getCostTime();
                                Logger.d(Constants.TAG, MSG + "remaining item => remaining: " + (bean.get(i).getPlanTime() - bean.get(i).getCostTime()) + " => after: " + ParseTime.msToHourMin(bean.get(i).getPlanTime() - bean.get(i).getCostTime()));
                                Logger.d(Constants.TAG, MSG + "total remaining time: " + longRemainingTaskCostTimeWeekly + " => after: " + ParseTime.msToHourMin(longRemainingTaskCostTimeWeekly));
                            }

                        }
                    }


                    // (3-2) book a complete message for current task (remaining time for current task) (one time job)
                    // (3.2.0) 撈當前這個 task plan time 多久，今天已經做了多久 (summary result)，並算出來多久以後要提醒完成
                    // (3.2.1) 如果 task 在今天來不及做完，或是已經超過 plan_time 了，就不會起這個 job for notification
                    // (3.2.2) 如果 task 來得及做完，就會起這個 job for notification (但不用 repeat)

                    // longCompleteTaskCostTimeDaily > 0:
                    //      當前 task 還沒消耗完 plan_time，才需要提醒 (如果 plan_time < cost_time 則不會進來
                    // (24:00-curTime) > longCompleteTaskCostTimeDaily
                    //      且今天來得及做完，才提醒


                    Logger.d(Constants.TAG, MSG + "start completejob => complete after: " + ParseTime.msToHourMin(longCompleteTaskCostTimeDaily));
                    Logger.d(Constants.TAG, MSG + "start completejob => today left: " + ParseTime.msToHourMin(ParseTime.getNextDailyNotifyMills(Constants.NOTIFICATION_TIME_DAILY_DATA_VERGEN)));
                    if (longCompleteTaskCostTimeDaily > 0
                            && ParseTime.getNextDailyNotifyMills(Constants.NOTIFICATION_TIME_DAILY_DATA_VERGEN) > longRemainingTaskCostTimeDaily) {

                        TimeManagementApplication.startJobScheduler(Constants.SCHEDULE_JOB_ID_COMPLETE_TASK,
                                JobSchedulerServiceComplete.class.getName(),
                                longCompleteTaskCostTimeDaily,   // 當前 task 還需要多久做完
                                true);
                    }


                    // (3-3) book a remaining tasks notification (one time job)

                    // if 今天剩下時間 > 待進行的項目總時間
                    //      (24:00-curTime) > longRemainingTaskCostTimeDaily ，那就 (24:00-curTime) - longRemainingTaskCostTimeDaily 之後再提醒
                    // else 不然就現在提醒該換件事情做
                    //      仍是 (24:00-curTime) - longRemainingTaskCostTimeDaily

                    // 還有 task 沒進行完，才需要提醒
                    Logger.d(Constants.TAG, MSG + "start remaining task reminder => need : " + ParseTime.msToHourMin(longRemainingTaskCostTimeDaily));
                    Logger.d(Constants.TAG, MSG + "start remaining task reminder => today left: " + ParseTime.msToHourMin(ParseTime.getNextDailyNotifyMills(Constants.NOTIFICATION_TIME_DAILY_DATA_VERGEN)));
                    if (longRemainingTaskCostTimeDaily > 0) {

                        TimeManagementApplication.startJobScheduler(Constants.SCHEDULE_JOB_ID_REMAINING_TASK,
                                JobSchedulerServiceRemainingTask.class.getName(),                                        // [TODO] Change to Remaining schedulerJob
                                ParseTime.getNextDailyNotifyMills(Constants.NOTIFICATION_TIME_DAILY_DATA_VERGEN) - longRemainingTaskCostTimeDaily,   // 剩餘項目還需要多久，就設定距離今天結束多久以前要提醒
                                true);
                    }

                    // TODO add weekly
                    setLoadingResultSummary(false);
                }

                @Override
                public void onError(String errorMessage) {

                    setLoadingResultSummary(false);
                    Logger.e(Constants.TAG, MSG + "GetResultDailySummary onError, errorMessage: " + errorMessage);

                }
            }).execute();

        }
    }

    public void setLoadingResultSummary(boolean loadingResultSummary) {
        mLoadingResultSummary = loadingResultSummary;
    }

    public boolean isLoadingResultSummary() {
        return mLoadingResultSummary;
    }



    // 立即發送一個 notification
    private void startNotificationOngoing(String strTitle, String strSubText, String strContent) {

        // Large Icon 作法 1
        Drawable drawable = ContextCompat.getDrawable(TimeManagementApplication.getAppContext(), TimeManagementApplication.getIconResourceId(Constants.APP_ICON_BIG));
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Large Icon 作法 2
//                Bitmap largeIcon = BitmapFactory.decodeResource(
//                        getResources(), R.drawable.icon_sleep);

//                // 建立通知物件 作法 1
//                Notification notification = builder.build();
//                // 使用BASIC_ID為編號發出通知
//                manager.notify(BASIC_ID, notification);

        // 建立通知物件 作法 2: 透過 builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(TimeManagementApplication.getAppContext(), Constants.NOTIFICATION_CHANNEL_ID_ONGOING);

        // 設置小圖標
        builder.setSmallIcon(TimeManagementApplication.getIconResourceId(Constants.APP_ICON_SMALL));
        // 設置大圖標
        builder.setLargeIcon(bitmap);

        // 標題
        builder.setContentTitle(strTitle);
        // 內容
        builder.setContentText(strContent);
        // 摘要
        builder.setSubText(strSubText);

        // 點擊後清除
        builder.setAutoCancel(true);
        //显示指定文本

        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        // 提醒時間，單位是毫秒 (1/1000s)
        builder.setWhen(System.currentTimeMillis());             // 設定為當下立刻啟動
//        mBuilder.setWhen(System.currentTimeMillis() - 3600000);   // 設定為系统時間少一小時 (會立刻叫)
//        mBuilder.setWhen(System.currentTimeMillis() + 3000 );        // 設定為 3 秒後

        // setOngoing(true) 設定為正在進行的通知，用戶無法清除 (類似 Foreground Service 通知)
        builder.setOngoing(false);

        // 設定提醒方式
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);       // 三種方式一起

        // 設置震動方式，延遲零秒，震動一秒，延遲一秒、震動一秒
        builder.setVibrate(new long[]{0, 1000, 1000, 1000});

        Intent intent = new Intent(TimeManagementApplication.getAppContext(), MainActivity.class);

        // PendingIntent 作法 1
        PendingIntent pendingIntent = PendingIntent.getActivity(TimeManagementApplication.getAppContext(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        // PendingIntent 作法 2
//// Creates an explicit intent for an Activity in your app
//                Intent resultIntent = new Intent(TimeManagementApplication.getAppContext(), MainActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//                TaskStackBuilder stackBuilder = TaskStackBuilder.create(TimeManagementApplication.getAppContext());
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
                (NotificationManager) TimeManagementApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);


        // Android 8.0 Oreo (APK 26) 以上必須設置通知頻道 Notification channels
        NotificationChannel channel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID_ONGOING,             // 此 ID 用來分辨不同的通知頻道
                    Constants.NOTIFICATION_CHANNEL_NAME_ONGOING,
                    NotificationManager.IMPORTANCE_HIGH);   // 設為 IMPORTANCE_HIGH 以上才會在上方懸掛跳出來

            notificationManager.createNotificationChannel(channel);    // 使用 NotificationManager 加入這個頻道

            builder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID_ONGOING);  // 呼叫 setChannelId 通知這個 Notification 的所屬頻道 ID

        } else {

            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        notificationManager.notify(Constants.NOTIFY_ID_ONGOING, builder.build());   // 用 notify 並指定 ID，隨後可用此 ID 做進一步的更新或是取消等等操作
    }

}

