package com.realizeitstudio.deteclife.trace.daily;


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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.realizeitstudio.deteclife.task.CategoryTaskListAdapter;
import com.realizeitstudio.deteclife.task.CategoryTaskListContract;
import com.realizeitstudio.deteclife.task.CategoryTaskListPresenter;
import com.realizeitstudio.deteclife.dml.GetCategoryTaskList;
import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTime;
import com.realizeitstudio.deteclife.MainActivity;
import com.realizeitstudio.deteclife.R;
import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/10/02
 *
 * A simple {@link Fragment} subclass.
 */
public class TraceDailyFragment extends Fragment implements TraceDailyContract.View, CategoryTaskListContract.View {

    private static final String MSG = "TraceDailyFragment: ";


    private LinearLayout mLinearLayoutPeriod;

    private CategoryTaskListContract.Presenter mCategroyTaskListContractPresenter;
    private CategoryTaskListAdapter mCategoryTaskListAdapter;
    private AlertDialog mDialog;

    private TraceDailyContract.Presenter mPresenter;
    private TraceDailyAdapter mTraceDailyAdapter;
    private int mIntTraceMode;
    private int mIntTaskMode;
    private PieChart mPieChart;

    public TraceDailyFragment() {
        // Required empty public constructor
    }

    public static com.realizeitstudio.deteclife.trace.daily.TraceDailyFragment newInstance() {
        return new com.realizeitstudio.deteclife.trace.daily.TraceDailyFragment();
    }

    @Override
    public void setPresenter(TraceDailyContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//[TODO] TraceDailyFragment onCreate
//        ((MainActivity) getActivity()).showUserInfoLog();
        mTraceDailyAdapter = new TraceDailyAdapter(new ArrayList<GetTaskWithPlanTime>(), mPresenter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_trace_daily, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_trace_daily);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));
        recyclerView.setAdapter(mTraceDailyAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mPresenter.onScrollStateChanged(
                        recyclerView.getLayoutManager().getChildCount(),
                        recyclerView.getLayoutManager().getItemCount(),
                        newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mPresenter.onScrolled(recyclerView.getLayoutManager());
            }
        });


        // [TODO] delete this part
        mLinearLayoutPeriod = root.findViewById(R.id.linearlayout_trace_period_daily);
        mLinearLayoutPeriod.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Logger.d(Constants.TAG, MSG + "Create a notification");


            }   // end of onClick
        });



        // Get the piechart
        mPieChart = root.findViewById(R.id.piechart_analysis_top_item);


        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start();
    }

    @Override
    public void showTaskListWithTraceTime(List<GetTaskWithPlanTime> bean) {
        mTraceDailyAdapter.updateData(bean);

        setupPieChart();
    }

    private void setupPieChart() {
        // Populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();

        pieEntries.add(new PieEntry(10f, "Green"));   // label is just a string
        pieEntries.add(new PieEntry(26.7f, "Yellow"));
        pieEntries.add(new PieEntry(24.0f, "Red"));
        pieEntries.add(new PieEntry(30.8f, "Blue"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Statistic Results");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setSliceSpace(5f);   // 每塊扇形之間的間距
        PieData pieData = new PieData(pieDataSet);


        mPieChart.setData(pieData);
//        mPieChart.setTransparentCircleAlpha(0); // 設定中心透明圓形的透明度 (0-255)

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.animateY(1000);
        mPieChart.invalidate(); // refresh pie chart

    }


    @Override
    public void refreshUi(int mode) {
        setIntTraceMode(mode);
        mTraceDailyAdapter.refreshUiMode(mode);
    }


    @Override
    public void showSetTargetUi() {
        ((MainActivity) getActivity()).transToSetTarget();
    }



    public int getIntTraceMode() {
        return mIntTraceMode;
    }

    public void setIntTraceMode(int intTraceMode) {
        mIntTraceMode = intTraceMode;
    }




    @Override
    public void setCategoryTaskListPresenter(CategoryTaskListContract.Presenter presenter) {
        mCategroyTaskListContractPresenter = checkNotNull(presenter);
    }

    @Override
    public void showTaskListDialog() {

        // ****** 用預設的 mDialog 介面 ******
        final String[] list_String = {"1", "2", "3", "4", "5"};

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("標題");
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setItems(list_String, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface mDialog, int which) {    // 傳回的 which 表示點擊列表的第幾項
//                Toast.makeText(getActivity(), "點擊: " + list_String[which], Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        AlertDialog mDialog = builder.create();
//        mDialog.show();

        if (mCategroyTaskListContractPresenter == null) {
            mCategroyTaskListContractPresenter = new CategoryTaskListPresenter(this);
        }


        // ****** 用自定義的 mDialog 介面 ******
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_categorytask_list, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_category_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeManagementApplication.getAppContext()));

        mCategoryTaskListAdapter = new CategoryTaskListAdapter(new ArrayList<GetCategoryTaskList>(), mCategroyTaskListContractPresenter);
        recyclerView.setAdapter(mCategoryTaskListAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(TimeManagementApplication.getAppContext(), DividerItemDecoration.VERTICAL));


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mCategroyTaskListContractPresenter.onScrollStateChanged(
                        recyclerView.getLayoutManager().getChildCount(),
                        recyclerView.getLayoutManager().getItemCount(),
                        newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mCategroyTaskListContractPresenter.onScrolled(recyclerView.getLayoutManager());
            }
        });

        mCategroyTaskListContractPresenter.start();

        builder.setView(view);
//        builder.setCancelable(true);
//        TextView title= (TextView) view
//                .findViewById(R.id.title);        // 設置標題
//        EditText input_edt= (EditText) view
//                .findViewById(R.id.dialog_edit);  // 輸入内容
//        Button btn_cancel=(Button)view
//                .findViewById(R.id.btn_cancel);   // 取消按鈕
//        Button btn_comfirm=(Button)view
//                .findViewById(R.id.btn_comfirm);  // 確定按鈕

        // 取消或確定按鈕監聽事件處理
        mDialog = builder.create();
        mDialog.show();
        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);

    }

    @Override
    public void showCategoryTaskList(List<GetCategoryTaskList> bean) {
        mCategoryTaskListAdapter.updateData(bean);
    }

    @Override
    public void showCategoryTaskSelected(GetCategoryTaskList bean) {
        mDialog.dismiss();

        Logger.d(Constants.TAG, MSG + "Category: " + bean.getCategoryName() + " Task: " + bean.getTaskName());
        mTraceDailyAdapter.showCategoryTaskSelected(bean);
    }

    @Override
    public void refreshCategoryTaskUi(int mode) {
        setIntTaskMode(mode);
        mCategoryTaskListAdapter.refreshUiMode(mode);
    }

    @Override
    public void showCategoryListDialog() {

    }

    public int getIntTaskMode() {
        return mIntTaskMode;
    }

    public void setIntTaskMode(int intTaskMode) {
        mIntTaskMode = intTaskMode;
    }


    private void startNotification() {
        int id = 9487;  // 設一個自己知道的 ID 就可以
        String TEST_NOTIFY_ID = "test"; // 通知頻道的 ID

        // Large Icon 作法 1
        Drawable drawable = ContextCompat.getDrawable(getActivity(), TimeManagementApplication.getIconResourceId(Constants.APP_ICON_BIG));
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Large Icon 作法 2
//                Bitmap largeIcon = BitmapFactory.decodeResource(
//                        getResources(), R.drawable.icon_sleep);

//                // 建立通知物件 作法 1
//                Notification notification = builder.build();
//                // 使用BASIC_ID為編號發出通知
//                manager.notify(BASIC_ID, notification);

        // 建立通知物件 作法 2: 透過 builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), TEST_NOTIFY_ID);

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
        RemoteViews contentView = new RemoteViews(getActivity().getPackageName(), R.layout.notification_daily);
        contentView.setImageViewResource(R.id.imageview_notificaiton_task_icon, R.drawable.icon_computer);
        contentView.setTextViewText(R.id.textview_daily_summary_title, "This is a custom layout");
        mBuilder.setContent(contentView);               // 如果不用 Builder 可以寫 notification.contentView = contentView;
        mBuilder.setCustomBigContentView(contentView);  // 可以設定通知縮起來的 layout（setContent) 和通知展開的 layout (setCustomBigContentView) (optional)
        mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());   // 加上這句，通知會有系統預設的框架。如果想要完全自訂就把這句拿掉
        mBuilder.setStyle(new android.support.v4.app.NotificationCompat.DecoratedCustomViewStyle());

        Intent intent = new Intent(getActivity(), MainActivity.class);

        // PendingIntent 作法 1
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
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
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);




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

