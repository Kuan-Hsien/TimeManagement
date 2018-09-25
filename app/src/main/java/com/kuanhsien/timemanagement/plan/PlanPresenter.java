package com.kuanhsien.timemanagement.plan;

import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.kuanhsien.timemanagement.GetTaskWithPlanTimeCallback;
import com.kuanhsien.timemanagement.GetTasksWithPlanAsyncTask;
import com.kuanhsien.timemanagement.object.CategoryDefineTable;
import com.kuanhsien.timemanagement.GetTaskWithPlanTime;
import com.kuanhsien.timemanagement.object.TaskDefineTable;
import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.object.TimePlanningTable;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/9/24.
 */
public class PlanPresenter implements PlanContract.Presenter {
    private static final String MSG = "PlanPresenter: ";

    private final PlanContract.View mPlanView;

    private String mStrStartTime;
    private String mStrEndTime;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;



    public PlanPresenter(PlanContract.View mainView) {
        mPlanView = checkNotNull(mainView, "planView cannot be null!");
        mPlanView.setPresenter(this);

        // [TODO] 要改為用畫面上的元件讀取
        // (e.g)
//        // 取得現在時間
//        Date curDate = new Date();
//        // 定義時間格式
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh24mm");
//        // 透過SimpleDateFormat的format方法將 Date 轉為字串
//        String strCurrentTime = simpleDateFormat.format(curDate);


        // 取得現在時間
        Date currentTime = new Date();
        mStrStartTime = new SimpleDateFormat("yyyy/MM/dd").format(currentTime); // 擷取到日期

        // 新增一個Calendar,並且指定時間
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR, 24);    // +24 小時

        Date tomorrowNow = calendar.getTime();  // 取得 24 小時後的現在時間
        mStrEndTime = new SimpleDateFormat("yyyy/MM/dd").format(tomorrowNow);   // 擷取到日期
    }

    @Override
    public void start() {
//        prepareRoomDatabase();
        getTaskWithPlanTime();
    }


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


    @Override
    public void getTaskWithPlanTime() {
        if (!isLoading()) {
            setLoading(true);

            new GetTasksWithPlanAsyncTask(
                    Constants.MODE_PERIOD, mStrStartTime, mStrEndTime, new GetTaskWithPlanTimeCallback() {

                @Override
                public void onCompleted(List<GetTaskWithPlanTime> bean) {
                    setLoading(false);
                    showTaskListWithPlanTime(bean);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, "GetTaskWithPlanTime.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }


    @Override
    public void showTaskListWithPlanTime(List<GetTaskWithPlanTime> bean) {
        mPlanView.showTaskListWithPlanTime(bean);
    }

    @Override
    public void showSetTargetUi() {
        mPlanView.showSetTargetUi();
    }


    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }


    public void getTaskWithPlanTime_ROOM() {

        // 和 Database 有關的操作不能放在 main-thread 中。不然會跳出錯誤：
        // Cannot access database on the main thread since it may potentially lock the UI for a long period of time.

        // 解決方式：(此處使用 2)
        // 1. 在取得資料庫連線時增加 allowMainThreadQueries() 方法，強制在主程式中執行
        // 2. 另開 thread 執行耗時工作 (建議採用此方法)，另開 thread 有多種寫法，按自己習慣作業即可。此處為測試是否寫入手機SQLite，故不考慮 callback，如下
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

//                 [INSERT]
//                CategoryDefineTable categoryItem = new CategoryDefineTable(1, "Work", true, "Red", "Work", "High");
//                TaskDefineTable taskItem = new TaskDefineTable(1, "Prepare final test", "Work", true);
//
//                dao.addCategory(categoryItem);
//                dao.addTask(taskItem);
//                dao.insertAllCategory();
//                dao.insertAll(2, "First name", "Last name", "Address", null);


                // [QUERY]
                // 可以在這邊撈，目前寫在這邊可以撈出來當前塞進去的資料。
                List<GetTaskWithPlanTime> taskWithPlanTimeList = dao.getTaskListWithPlanTime(Constants.MODE_PERIOD, mStrStartTime, mStrEndTime);

                for (int i = 0 ; i < taskWithPlanTimeList.size() ; ++i) {
                    Logger.d(Constants.TAG, taskWithPlanTimeList.get(i).getTaskName() + " " );
                }

                List<GetTaskWithPlanTime> allTaskWithPlanTimeList = dao.getAllTaskListWithPlanTime(Constants.MODE_PERIOD, mStrStartTime, mStrEndTime);

                for (int i = 0 ; i < allTaskWithPlanTimeList.size() ; ++i) {
                    Logger.d(Constants.TAG, allTaskWithPlanTimeList.get(i).getTaskName() + " " );
                }

                showTaskListWithPlanTime(allTaskWithPlanTimeList);
            }
        });
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

                DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

                // [INSERT]
//                CategoryDefineTable categoryItem = new CategoryDefineTable(1, "Work", true, "Red", "Work", "High");
                dao.addTask(new TaskDefineTable("HEALTH","Sleep", "RED", "SLEEP", false));
                dao.addTask(new TaskDefineTable("STUDY", "Study", "BLUE", "BOOK", false));
                dao.addTask(new TaskDefineTable("RELATIONSHIP","Family", "ORANGE", "HOME", false));
                dao.addTask(new TaskDefineTable("WORK","Work", "BLACK", "WORK", false));
                dao.addTask(new TaskDefineTable("HEALTH","Eat", "GREEN", "FOOD", false));
                dao.addTask(new TaskDefineTable("SELF", "Re-arrange Time", "YELLOW", "CLOCK", false));
                dao.addTask(new TaskDefineTable("RELATIONSHIP", "Friends", "LIGHT-BLUE", "PEOPLE", false));
                dao.addTask(new TaskDefineTable("HEALTH", "Toilet", "BROWN", "TOILET", false));
                dao.addTask(new TaskDefineTable("OTHERS","Transportation", "WHITE", "CAR", false));
                dao.addTask(new TaskDefineTable("SELF","Free", "GREY", "MAN", true));

                // 取得現在時間
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleDateFormat.format(curDate);

                dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "HEALTH", "Sleep", strCurrentTime, strCurrentTime, "8 hr"));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "HEALTH", "Eat", strCurrentTime, strCurrentTime, "2 hr"));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "RELATIONSHIP", "Family", strCurrentTime, strCurrentTime, "2 hr"));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "RELATIONSHIP", "Family", strCurrentTime, strCurrentTime, "2 hr"));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "HEALTH", "Toilet", strCurrentTime, strCurrentTime, "2 hr"));

//                dao.addCategory(categoryItem);
//                dao.addTask(taskItem);
//                dao.insertAllCategory();
//                dao.insertAll(2, "First name", "Last name", "Address", null);


                // [QUERY]
                // 可以在這邊撈，目前寫在這邊可以撈出來當前塞進去的資料。
                List<TimePlanningTable> planningTableList = dao.getAllPlanList();
                List<TaskDefineTable> taskList = dao.getTaskList();

                for (int i = 0 ; i < planningTableList.size() ; ++i) {
                    Logger.d(Constants.TAG, MSG + "i = " + i +
                            ", Mode: " + planningTableList.get(i).getMode() +
                            ", Category: " + planningTableList.get(i).getCategoryName() +
                            ", Task: " + planningTableList.get(i).getTaskName() +
                            ", StartTime: " + planningTableList.get(i).getStartTime() +
                            ", EndTime: " + planningTableList.get(i).getEndTime() +
                            ", CostTime: " + planningTableList.get(i).getCostTime());
                }

                for (int i = 0 ; i < taskList.size() ; ++i) {
                    Logger.d(Constants.TAG, MSG + "i = " + i +
                            ", Category: " + taskList.get(i).getCategoryName() +
                            ", Task: " + taskList.get(i).getTaskName());
                }

            }
        });
    }
}
