package com.kuanhsien.timemanagement.plan.daily;

import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kuanhsien.timemanagement.GetTaskWithPlanTimeCallback;
import com.kuanhsien.timemanagement.GetTaskWithPlanTimeAsyncTask;
import com.kuanhsien.timemanagement.SetTargetAsyncTask;
import com.kuanhsien.timemanagement.SetTargetCallback;
import com.kuanhsien.timemanagement.GetTaskWithPlanTime;
import com.kuanhsien.timemanagement.object.CategoryDefineTable;
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
public class PlanDailyPresenter implements PlanDailyContract.Presenter {
    private static final String MSG = "PlanDailyPresenter: ";

    private final PlanDailyContract.View mPlanView;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;


    public PlanDailyPresenter(PlanDailyContract.View mainView) {
        mPlanView = checkNotNull(mainView, "planView cannot be null!");
        mPlanView.setPresenter(this);
    }

    @Override
    public void start() {
        prepareRoomDatabase();
//        getTaskWithPlanTime();
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
        mPlanView.refreshUi(mode);
    }


    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getTaskWithPlanTime() {
        if (!isLoading()) {
            setLoading(true);

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
            String mStrStartTime = new SimpleDateFormat("yyyy/MM/dd").format(currentTime); // 擷取到日期

            // 新增一個Calendar,並且指定時間
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.HOUR, 24);    // +24 小時

            Date tomorrowNow = calendar.getTime();  // 取得 24 小時後的現在時間
            String mStrEndTime = new SimpleDateFormat("yyyy/MM/dd").format(tomorrowNow);   // 擷取到日期


            new GetTaskWithPlanTimeAsyncTask(
                    Constants.MODE_DAILY, mStrStartTime, mStrEndTime, new GetTaskWithPlanTimeCallback() {

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


    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showTaskListWithPlanTime(List<GetTaskWithPlanTime> bean) {
        mPlanView.showTaskListWithPlanTime(bean);
    }


    // 2-1. [Send-to-Model] database insert to update data (insert new targets or adjust time for existed targets)
    @Override
    public void saveTargetResults(List<TimePlanningTable> targetList, List<TimePlanningTable> deleteTargetList) {
//    public void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime) {

        // insert time_planning_table
        new SetTargetAsyncTask(targetList, deleteTargetList,  new SetTargetCallback() {

            @Override
            public void onCompleted(List<TimePlanningTable> bean) {

                Logger.d(Constants.TAG, MSG + "SetTarget onCompleted");
                for( int i = 0 ; i < bean.size() ; ++i) {
                    Logger.d(Constants.TAG, MSG + "TaskName: " + bean.get(i).getTaskName() + " Cost-time: " + bean.get(i).getCostTime());
                }

                // [TODO] insert 資料後更新畫面，目前是將要更新的資料全部當作 bean
                // 假如有順利 insert，則跳回 Plan Fragment，但是裡面的內容要更新 (重新撈取資料或是把所有更新項目都塞進 list 中，也包含 edit 的時間結果)
                // (1) 方法 1: 用 LiveData 更新
                // (2) 方法 2: 從這裡回到 PlanDailyFragment，或是回到 MainActivity > MainPresenter > PlanDailyFragment 更新
                // *(3) 方法 3: [TODO] 把 TimePlanningTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 GetTaskWithPlanTime 物件
                getTaskWithPlanTime();
            }

            @Override
            public void onError(String errorMessage) {

                Logger.d(Constants.TAG, MSG + "SetTarget onError, errorMessage: " + errorMessage);

                refreshUi(Constants.MODE_PLAN_VIEW);
            }
        }).execute();
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
//                dao.addCategory(new CategoryDefineTable("Health", false, "green", 1));
//                dao.addCategory(new CategoryDefineTable("Family", false, "red", 2));
//                dao.addCategory(new CategoryDefineTable("Personal", false, "yellow", 3));
//                dao.addCategory(new CategoryDefineTable("Friend", false, "orange", 4));
//                dao.addCategory(new CategoryDefineTable("Work", false, "blue", 5));
//                dao.addCategory(new CategoryDefineTable("Transportation", false, "grey", 6));
//                dao.addCategory(new CategoryDefineTable("Others", false, "pink", 7));

                dao.addCategory(new CategoryDefineTable("Health", false, "#32CD32", 1));
                dao.addCategory(new CategoryDefineTable("Family", false, "#C71585", 2));
                dao.addCategory(new CategoryDefineTable("Personal", false, "#FFD700", 3));
                dao.addCategory(new CategoryDefineTable("Friend", false, "#F4A460", 4));
                dao.addCategory(new CategoryDefineTable("Work", false, "#1E90FF", 5));
                dao.addCategory(new CategoryDefineTable("Transportation", false, "#B0C4DE", 6));
                dao.addCategory(new CategoryDefineTable("Others", false, "#4682B4", 7));

//                dao.addTask(new TaskDefineTable("Work", "Work", "blue", "icon_work", 8, false));
//                dao.addTask(new TaskDefineTable("Personal", "Study", "yellow", "icon_book", 7, false));
//                dao.addTask(new TaskDefineTable("Family", "Family", "red", "icon_home", 4, false));
//                dao.addTask(new TaskDefineTable("Friend", "Friend", "orange", "icon_friend", 6, false));
//                dao.addTask(new TaskDefineTable("Family", "Lover", "red", "icon_lover", 5, false));
//                dao.addTask(new TaskDefineTable("Health", "Sleep", "green", "icon_sleep", 1, false));
//                dao.addTask(new TaskDefineTable("Health", "Eat", "green", "icon_food", 2, false));
//                dao.addTask(new TaskDefineTable("Health", "Swim", "green", "icon_swim", 3, false));
//                dao.addTask(new TaskDefineTable("Transportation", "Walk", "grey", "icon_walk", 13, false));
//                dao.addTask(new TaskDefineTable("Transportation", "Car", "grey", "icon_car", 15, false));
//                dao.addTask(new TaskDefineTable("Transportation", "Bike", "grey", "icon_bike", 14, false));
//                dao.addTask(new TaskDefineTable("Others", "Computer", "pink", "icon_computer", 12, false));
//                dao.addTask(new TaskDefineTable("Others", "Music", "pink", "icon_music", 10, false));
//                dao.addTask(new TaskDefineTable("Others", "Pet", "pink", "icon_paw", 9, false));
//                dao.addTask(new TaskDefineTable("Others", "Phone", "pink", "icon_phonecall", 11, false));

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

                // 取得現在時間
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strCurrentTime = simpleDateFormat.format(curDate);

                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Sleep", strCurrentTime, strCurrentTime, 480, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Eat", strCurrentTime, strCurrentTime, 120, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Family", "Family", strCurrentTime, strCurrentTime, 120, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_WEEKLY, "Family", "Family", strCurrentTime, strCurrentTime, 1200, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Personal", "Study", strCurrentTime, strCurrentTime, 75, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Friend", "Friend", strCurrentTime, strCurrentTime, 75, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Health", "Swim", strCurrentTime, strCurrentTime, 75, strCurrentTime));
                dao.addPlanItem(new TimePlanningTable(Constants.MODE_DAILY, "Others", "Music", strCurrentTime, strCurrentTime, 75, strCurrentTime));

//                dao.addCategory(categoryItem);
//                dao.addTask(taskItem);
//                dao.insertAllCategory();
//                dao.insertAll(2, "First name", "Last name", "Address", null);


                // [QUERY]
                // 可以在這邊撈，目前寫在這邊可以撈出來當前塞進去的資料。
                List<CategoryDefineTable> categoryList = dao.getAllCategoryList();
                List<TaskDefineTable> taskList = dao.getAllTaskList();
                List<TimePlanningTable> planningTableList = dao.getAllPlanList();


                for (int i = 0 ; i < categoryList.size() ; ++i) {
                    Logger.d(Constants.TAG, MSG + "i = " + i +
                            ", Category: " + categoryList.get(i).getCategoryName());
                }


                for (int i = 0 ; i < taskList.size() ; ++i) {
                    Logger.d(Constants.TAG, MSG + "i = " + i +
                            ", Category: " + taskList.get(i).getCategoryName() +
                            ", Task: " + taskList.get(i).getTaskName());
                }


                for (int i = 0 ; i < planningTableList.size() ; ++i) {
                    Logger.d(Constants.TAG, MSG + "i = " + i +
                            ", Mode: " + planningTableList.get(i).getMode() +
                            ", Category: " + planningTableList.get(i).getCategoryName() +
                            ", Task: " + planningTableList.get(i).getTaskName() +
                            ", StartTime: " + planningTableList.get(i).getStartTime() +
                            ", EndTime: " + planningTableList.get(i).getEndTime() +
                            ", CostTime: " + planningTableList.get(i).getCostTime());
                }
            }
        });
    }

    @Override
    public void showCategoryListDialog() {
        mPlanView.showCategoryListDialog();
    }

    @Override
    public void showTaskListDialog() {
        mPlanView.showTaskListDialog();
    }
}
