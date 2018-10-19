package com.realizeitstudio.deteclife.iconpicker;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.realizeitstudio.deteclife.addtask.AddTaskPresenter;
import com.realizeitstudio.deteclife.dml.GetIconListAsyncTask;
import com.realizeitstudio.deteclife.dml.GetIconListCallback;
import com.realizeitstudio.deteclife.dml.SetTaskAsyncTask;
import com.realizeitstudio.deteclife.dml.SetTaskCallback;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/10/18
 */
public class IconPickerPresenter implements IconPickerContract.Presenter {
    private static final String MSG = "IconPickerPresenter: ";

    private final IconPickerContract.View mTaskView;

    private AddTaskPresenter mAddTaskPresenter;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;


    public IconPickerPresenter(IconPickerContract.View mainView, AddTaskPresenter addTaskPresenter) {
        mTaskView = checkNotNull(mainView, "IconPickerView cannot be null!");
        mTaskView.setPresenter(this);

        mAddTaskPresenter = addTaskPresenter;

    }

    @Override
    public void start() {
        getIconList();
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
        mTaskView.refreshUi(mode);
    }


    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getIconList() {
        if (!isLoading()) {
            setLoading(true);

            new GetIconListAsyncTask(new GetIconListCallback() {

                @Override
                public void onCompleted(List<IconDefineTable> bean) {
                    setLoading(false);
                    showIconList(bean);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, "GetIconListAsyncTask.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }


    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showIconList(List<IconDefineTable> bean) {
        mTaskView.showIconList(bean);
    }

    @Override
    public void showIconSelected(IconDefineTable bean) {

        mTaskView.closeDialog();   // call view to close dialog

        Logger.d(Constants.TAG, MSG + "choose icon: ");
        bean.LogD();

        mAddTaskPresenter.showIconSelected(bean);
    }

    // 2-1. [Send-to-Model] database insert to update data (insert new targets or adjust time for existed targets)
//    @Override
//    public void saveTaskResults(List<TaskDefineTable> targetList, List<TaskDefineTable> deleteTargetList) {
////    public void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime) {
//
//        // insert time_planning_table
//        new SetTaskAsyncTask(targetList, deleteTargetList,  new SetTaskCallback() {
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
//                // *(3) 方法 3: [TODO] 把 TaskDefineTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 IconDefineTable 物件
//                getIconList();
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


    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }


    // [TODO] 未來進階版也許可以上傳自己的 icon (但應該先做從 firebase 上更新圖示和圖片名稱這步)
//    @Override
//    public void showCategoryListDialog() {
//        mTaskView.showCategoryListDialog();
//    }


}
