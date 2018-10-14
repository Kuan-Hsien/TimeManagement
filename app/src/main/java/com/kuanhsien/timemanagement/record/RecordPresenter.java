package com.kuanhsien.timemanagement.record;


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

import com.kuanhsien.timemanagement.MainActivity;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskListAsyncTask;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskListCallback;
import com.kuanhsien.timemanagement.dml.GetCurrentTraceTaskAsyncTask;
import com.kuanhsien.timemanagement.dml.GetCurrentTraceTaskCallback;
import com.kuanhsien.timemanagement.dml.GetTraceSummary;
import com.kuanhsien.timemanagement.dml.SetRecordAsyncTask;
import com.kuanhsien.timemanagement.dml.SetRecordCallback;
import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.object.TimeTracingTable;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;
import com.kuanhsien.timemanagement.utils.ParseTime;

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

//                loadArticles();

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

                    for (int i = 0 ; i < bean.size() ; ++i) {
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



//    @Override
//    public void showCategoryTaskSelected(GetCategoryTaskList bean) {
//        mRecordView.showCategoryTaskSelected(bean);
//    }

    // 2-1. [Send-to-Model] database insert to update data (insert new records or adjust time for existed records)
//    @Override
//    public void saveTaskResults(List<TaskDefineTable> recordList, List<TaskDefineTable> deleteTargetList) {
////    public void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime) {
//
//        // insert time_planning_table
//        new SetTaskAsyncTask(recordList, deleteTargetList,  new SetTaskCallback() {
//
//            @Override
//            public void onCompleted(List<TaskDefineTable> bean) {
//
//                Logger.d(Constants.TAG, MSG + "SetTask onCompleted");
//                for( int i = 0 ; i < bean.size() ; ++i) {
//                    bean.get(i).LogD();
//                }
//
//                // [TODO] insert 資料後更新畫面，目前是將要更新的資料全部當作 bean
//                // 假如有順利 insert，則跳回 Plan Fragment，但是裡面的內容要更新 (重新撈取資料或是把所有更新項目都塞進 list 中，也包含 edit 的時間結果)
//                // (1) 方法 1: 用 LiveData 更新
//                // (2) 方法 2: 從這裡回到 PlanDailyFragment，或是回到 MainActivity > MainPresenter > PlanDailyFragment 更新
//                // *(3) 方法 3: [TODO] 把 TaskDefineTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 GetCategoryTaskList 物件
//                getCategoryTaskList();
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//
//                Logger.d(Constants.TAG, MSG + "SetTask onError, errorMessage: " + errorMessage);
//
//                refreshUi(Constants.MODE_PLAN_VIEW);
//            }
//        }).execute();
//    }


    @Override
    public void saveTraceResults(final List<TimeTracingTable> recordList, String startVerNo, String endVerNo, String categoryList, String taskList) {

        // insert time_tracing_table
        new SetRecordAsyncTask(recordList, startVerNo, endVerNo, categoryList, taskList, new SetRecordCallback() {

            // after insert a new record, need to get the current trace summary and show in notification
            @Override
            public void onCompleted(List<GetTraceSummary> bean) {

                Logger.d(Constants.TAG, MSG + "SetRecord onCompleted");
                for( int i = 0 ; i < bean.size() ; ++i) {
                    bean.get(i).LogD();
                }
                // [TODO] 更新 Record Fragment 內容，getCategoryTaskList 和 getCurrentTraceItem 其實都可以不做，應該是近來這頁的時候重撈就可以。同時 getCurrentTraceItem 其實不用重撈，可以從傳入要 save 資料的 bean 去抓 currentTraceItem
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
                // (3-2) remaining time for current task

                // Notifcaiton title/ subtext/ content
                // [TODO] daily total
                String strTitle = "Save " + ParseTime.msToHourMinDiff(recordList.get(0).getStartTime(), recordList.get(0).getEndTime()) + " to " + recordList.get(0).getTaskName();
                String strSubtext = "Current task: " + recordList.get(recordList.size()-1).getTaskName(); // last element would be the current tracing item
                String strContent = "Today's total: ";
                for (int i = 0 ; i < bean.size() ; ++i) {
                    strContent += bean.get(i).getTaskName() + " " + ParseTime.msToHourMin(bean.get(i).getCostTime());
                }


                Logger.d(Constants.TAG, MSG + "Title: " + strTitle);
                Logger.d(Constants.TAG, MSG + "Subtext: " + strSubtext);
                Logger.d(Constants.TAG, MSG + "Content: " + strContent);

                startNotificationOngoing(strTitle, strSubtext, strContent);


                // [TODO] insert 資料後跳轉 Trace Fragemnt (該 Fragment 需要重新抓取資料)
                // (4) 從這裡回到 RecordFragment，回到 MainActivity > MainPresenter > TraceDailyFragment 更新
                mRecordView.showTraceUi();
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





    // 立即發送一個 notification
    private void startNotificationOngoing(String strTitle, String strSubText, String strContent) {

        // Large Icon 作法 1
        Drawable drawable = ContextCompat.getDrawable(TimeManagementApplication.getAppContext(), R.drawable.ic_launcher_xxhdpi);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Large Icon 作法 2
//                Bitmap largeIcon = BitmapFactory.decodeResource(
//                        getResources(), R.drawable.icon_sleep);

//                // 建立通知物件 作法 1
//                Notification notification = builder.build();
//                // 使用BASIC_ID為編號發出通知
//                manager.notify(BASIC_ID, notification);

        // 建立通知物件 作法 2: 透過 builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(TimeManagementApplication.getAppContext(), Constants.NOTIFICATION_CHANNEL_ID_ONGOING);

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

//        // 5. 自訂畫面
//        // Daily notification
//        RemoteViews contentView = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_daily);
////        contentView.setImageViewResource(R.id.imageview_notificaiton_task_icon_parent, R.drawable.icon_computer);
////        contentView.setTextViewText(R.id.textview_daily_summary_title, "Daily summary");
//
//        // 在 notification 中新增每一個 Task Items
//        RemoteViews remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_book);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Book");
//
//
//
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//
//
//
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//
//
//        // (1) set icon
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_sleep);
//        remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor("#F4A460"));
//
//        // (2) set task name
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name_complete, "Book");
//
//        // (2.1) set textView color
////        remoteViewsItem.setTextColor(R.id.textview_notification_task_name_complete, Color.parseColor("#AAAAAA"));
//
//        // (2.2) set textView strikethrough (刪除線)
////        remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);
//
//        // 最後決定完成項目的 style 直接用另一個 view 畫出來
//        remoteViewsItem.setInt(R.id.textview_notification_task_name_complete, "setVisibility", View.VISIBLE);
//        remoteViewsItem.setInt(R.id.textview_notification_cost_time_complete, "setVisibility", View.VISIBLE);
//        remoteViewsItem.setInt(R.id.textview_notification_task_name, "setVisibility", View.GONE);
//        remoteViewsItem.setInt(R.id.textview_notification_cost_time, "setVisibility", View.GONE);
//
//        remoteViewsItem.setInt(R.id.image_notification_strikethrough_complete, "setVisibility", View.VISIBLE);
//
//
//
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//
//
//
//
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//
////        gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.selector_notification_icon);
////        gradientDrawable.setColor(Color.parseColor("#393920"));
//
//
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_work);
//        remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor("#32CD32"));
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Work");
//
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor("#C71585"));
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_walk);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Walk");
//
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setInt(R.id.imageview_notificaiton_task_icon, "setColorFilter", Color.parseColor("#1E90FF"));
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_swim);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Swim");
//
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_drunk);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Drunk");
//
//        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_daily_summary, remoteViewsItem);
//
//
//
//
//
////Weekly
//
//
//        // 在 notification 中新增每一個 Task Items
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_book);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Bookabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
//
//
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_sleep);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Sleep");
//
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_work);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Work");
//
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_walk);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Walk");
//
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_swim);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Swim");
//
//        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//
//        remoteViewsItem = new RemoteViews(TimeManagementApplication.getAppContext().getPackageName(), R.layout.notification_item);
//        remoteViewsItem.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_drunk);
//        remoteViewsItem.setTextViewText(R.id.textview_notification_task_name, "Drunk");
//
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
////        contentView.addView(R.id.linearlayout_notification_week_summary, remoteViewsItem);
//
//
//
//
//        mBuilder.setContent(contentView);               // 如果不用 Builder 可以寫 notification.contentView = contentView;
//        mBuilder.setCustomBigContentView(contentView);  // 可以設定通知縮起來的 layout（setContent) 和通知展開的 layout (setCustomBigContentView) (optional)
//        mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());   // 加上這句，通知會有系統預設的框架。如果想要完全自訂就把這句拿掉
//        mBuilder.setStyle(new android.support.v4.app.NotificationCompat.DecoratedCustomViewStyle());



        Intent intent = new Intent(TimeManagementApplication.getAppContext(), MainActivity.class);

        // PendingIntent 作法 1
        PendingIntent pIntent = PendingIntent.getActivity(TimeManagementApplication.getAppContext(), 0, intent, 0);
        mBuilder.setContentIntent(pIntent);

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


        NotificationManager mNotificationManager =
                (NotificationManager) TimeManagementApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);


        // Android 8.0 Oreo (APK 26) 以上必須設置通知頻道 Notification channels
        NotificationChannel channel;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID_ONGOING,             // 此 ID 用來分辨不同的通知頻道
                    Constants.NOTIFICATION_CHANNEL_NAME_ONGOING,
                    NotificationManager.IMPORTANCE_HIGH);   // 設為 IMPORTANCE_HIGH 以上才會在上方懸掛跳出來

            mNotificationManager.createNotificationChannel(channel);    // 使用 NotificationManager 加入這個頻道

            mBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID_ONGOING);  // 呼叫 setChannelId 通知這個 Notification 的所屬頻道 ID

        } else {

            mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        mNotificationManager.notify(Constants.NOTIFY_ID_ONGOING, mBuilder.build());   // 用 notify 並指定 ID，隨後可用此 ID 做進一步的更新或是取消等等操作
    }

}

