package com.kuanhsien.timemanagement.record;


import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskListAsyncTask;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskListCallback;
import com.kuanhsien.timemanagement.dml.GetCurrentTraceTaskAsyncTask;
import com.kuanhsien.timemanagement.dml.GetCurrentTraceTaskCallback;
import com.kuanhsien.timemanagement.dml.GetTaskWithPlanTimeCallback;
import com.kuanhsien.timemanagement.dml.GetTaskWithPlanTimeAsyncTask;
import com.kuanhsien.timemanagement.dml.SetRecordAsyncTask;
import com.kuanhsien.timemanagement.dml.SetRecordCallback;
import com.kuanhsien.timemanagement.dml.SetTargetAsyncTask;
import com.kuanhsien.timemanagement.dml.SetTargetCallback;
import com.kuanhsien.timemanagement.dml.GetTaskWithPlanTime;
import com.kuanhsien.timemanagement.dml.SetTaskAsyncTask;
import com.kuanhsien.timemanagement.dml.SetTaskCallback;
import com.kuanhsien.timemanagement.object.CategoryDefineTable;
import com.kuanhsien.timemanagement.object.TaskDefineTable;
import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.object.TimePlanningTable;
import com.kuanhsien.timemanagement.object.TimeTracingTable;
import com.kuanhsien.timemanagement.task.CategoryTaskListContract;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

//        prepareRoomDatabase();


        // (1) Task List
        getCategoryTaskList();


        // (2) Current Trace Task
        // 取得現在時間
        Date curDate = new Date();
        // 定義時間格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
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
                    showCategoryTaskList(bean);
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
                    Logger.e(Constants.TAG, "GetGetCurrentTraceTask.onError, errorMessage: " + errorMessage);
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
    public void saveTraceResults(List<TimeTracingTable> recordList) {

        // insert time_planning_table
        new SetRecordAsyncTask(recordList,  new SetRecordCallback() {

            @Override
            public void onCompleted(List<TimeTracingTable> bean) {

                Logger.d(Constants.TAG, MSG + "SetRecord onCompleted");
                for( int i = 0 ; i < bean.size() ; ++i) {
                    bean.get(i).LogD();
                }
                // [TODO] 更新 Record Fragment 內容
                // (1) Task List
                getCategoryTaskList();


                // (2) Current Trace Task
                // 取得現在時間
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strVerNo = simpleDateFormat.format(curDate);

                getCurrentTraceItem(strVerNo);


                // [TODO] insert 資料後跳轉 Trace Fragemnt (該 Fragment 需要重新抓取資料)
                // 從這裡回到 RecordFragment，回到 MainActivity > MainPresenter > TraceDailyFragment 更新
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

    //    @Override
//    public void showCategoryListDialog() {
//        mRecordView.showCategoryListDialog();
//    }






    private void prepareRoomDatabase() {

        // 和 Database 有關的操作不能放在 main-thread 中。不然會跳出錯誤：
        // Cannot access database on the main thread since it may potentially lock the UI for a long period of time.

        // 解決方式：(此處使用 2)
        // 1. 在取得資料庫連線時增加 allowMainThreadQueries() 方法，強制在主程式中執行
        // 2. 另開 thread 執行耗時工作 (建議採用此方法)，另開 thread 有多種寫法，按自己習慣作業即可。此處為測試是否寫入手機SQLite，故不考慮 callback，如下
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                // 取得現在時間
                Date curDate = new Date();
                // 定義時間格式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                // 透過SimpleDateFormat的format方法將 Date 轉為字串
                String strVerNo = simpleDateFormat.format(curDate);

                DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

                dao.addTraceItem(new TimeTracingTable(strVerNo, "Health", "Sleep",  new Date().getTime(), null, null, new Date().getTime()));


                // [QUERY]
                // 可以在這邊撈，目前寫在這邊可以撈出來當前塞進去的資料。
                List<TimeTracingTable> traceList = dao.getAllTraceList();

                Logger.d(Constants.TAG, MSG + "Prepare first trace");

                for (int i = 0 ; i < traceList.size() ; ++i) {
                    traceList.get(i).LogD();
                }

            }
        });
    }


}

