package com.kuanhsien.timemanagement.task;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kuanhsien.timemanagement.dml.GetCategoryTaskList;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskListAsyncTask;
import com.kuanhsien.timemanagement.dml.GetCategoryTaskListCallback;
import com.kuanhsien.timemanagement.dml.SetTaskAsyncTask;
import com.kuanhsien.timemanagement.dml.SetTaskCallback;
import com.kuanhsien.timemanagement.object.TaskDefineTable;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CategoryTaskListPresenter implements CategoryTaskListContract.Presenter {
    private static final String MSG = "CategoryTaskListPresenter: ";

    private final CategoryTaskListContract.View mTaskView;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;


    public CategoryTaskListPresenter(CategoryTaskListContract.View mainView) {
        mTaskView = checkNotNull(mainView, "taskView cannot be null!");
        mTaskView.setCategoryTaskListPresenter(this);
    }

    @Override
    public void start() {
        getCategoryTaskList();
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
    public void refreshCategoryTaskUi(int mode) {
        mTaskView.refreshCategoryTaskUi(mode);
    }


    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getCategoryTaskList() {
        if (!isLoading()) {
            setLoading(true);

            // [TODO] 要改為用畫面上的元件讀取

            // 取得現在時間
            Date currentTime = new Date();
            String mStrStartTime = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO).format(currentTime); // 擷取到日期

            // 新增一個Calendar,並且指定時間
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.HOUR, 24);    // +24 小時

            Date tomorrowNow = calendar.getTime();  // 取得 24 小時後的現在時間
            String mStrEndTime = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO).format(tomorrowNow);   // 擷取到日期


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
        mTaskView.showCategoryTaskList(bean);
    }

    @Override
    public void showCategoryTaskSelected(GetCategoryTaskList bean) {
        mTaskView.showCategoryTaskSelected(bean);
    }

    // 2-1. [Send-to-Model] database insert to update data (insert new targets or adjust time for existed targets)
    @Override
    public void saveTaskResults(List<TaskDefineTable> targetList, List<TaskDefineTable> deleteTargetList) {
//    public void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime) {

        // insert time_planning_table
        new SetTaskAsyncTask(targetList, deleteTargetList,  new SetTaskCallback() {

            @Override
            public void onCompleted(List<TaskDefineTable> bean) {

                Logger.d(Constants.TAG, MSG + "SetTask onCompleted");
                for( int i = 0 ; i < bean.size() ; ++i) {
                    bean.get(i).LogD();
                }

                // [TODO] insert 資料後更新畫面，目前是將要更新的資料全部當作 bean
                // 假如有順利 insert，則跳回 Plan Fragment，但是裡面的內容要更新 (重新撈取資料或是把所有更新項目都塞進 list 中，也包含 edit 的時間結果)
                // (1) 方法 1: 用 LiveData 更新
                // (2) 方法 2: 從這裡回到 PlanDailyFragment，或是回到 MainActivity > MainPresenter > PlanDailyFragment 更新
                // *(3) 方法 3: [TODO] 把 TaskDefineTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 GetCategoryTaskList 物件
                getCategoryTaskList();
            }

            @Override
            public void onError(String errorMessage) {

                Logger.d(Constants.TAG, MSG + "SetTask onError, errorMessage: " + errorMessage);

                refreshCategoryTaskUi(Constants.MODE_PLAN_VIEW);
            }
        }).execute();
    }


    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }


    @Override
    public void showCategoryListDialog() {
        mTaskView.showCategoryListDialog();
    }

}
