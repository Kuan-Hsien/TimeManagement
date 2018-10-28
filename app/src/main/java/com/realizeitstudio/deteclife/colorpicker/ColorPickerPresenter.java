package com.realizeitstudio.deteclife.colorpicker;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.realizeitstudio.deteclife.dml.GetColorListAsyncTask;
import com.realizeitstudio.deteclife.dml.GetColorListCallback;
import com.realizeitstudio.deteclife.object.ColorDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/27
 */
public class ColorPickerPresenter implements ColorPickerContract.Presenter {
    private static final String MSG = "ColorPickerPresenter: ";

    private final ColorPickerContract.View mTaskView;

//    private String mStrCurTaskPage = "";

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;


//    public ColorPickerPresenter(ColorPickerContract.View mainView, String strCurTaskPage) {
    public ColorPickerPresenter(ColorPickerContract.View mainView) {
        mTaskView = checkNotNull(mainView, "ColorPickerView cannot be null!");

//        mTaskView.setPresenter(this);
//        setStrCurTaskPage(strCurTaskPage);
    }

    @Override
    public void start() {
        getColorList();
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

    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getColorList() {
        if (!isLoading()) {
            setLoading(true);

            new GetColorListAsyncTask(new GetColorListCallback() {

                @Override
                public void onCompleted(List<ColorDefineTable> bean) {
                    setLoading(false);
                    showColorList(bean);
                }

                @Override
                public void onError(String errorMessage) {
                    setLoading(false);
                    Logger.e(Constants.TAG, "GetColorListAsyncTask.onError, errorMessage: " + errorMessage);
                }
            }).execute();
        }
    }


    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showColorList(List<ColorDefineTable> bean) {
        mTaskView.showColorList(bean);
    }

    @Override
    public void showColorSelected(ColorDefineTable bean) {

//        mTaskView.closeDialog();   // call view to close dialog

        Logger.d(Constants.TAG, MSG + "choose color: ");
        bean.logD();

        // call current task page's view
//        if (Constants.PAGE_ADD_TASK.equals(getStrCurTaskPage())) {
        mTaskView.showColorSelected(bean);

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
//                for( int i = 0; i < bean.size(); ++i) {
//                    bean.get(i).logD();
//                }
//
//                // [TODO] insert 資料後更新畫面，目前是將要更新的資料全部當作 bean
//                // 假如有順利 insert，則跳回 Plan Fragment，但是裡面的內容要更新 (重新撈取資料或是把所有更新項目都塞進 list 中，也包含 edit 的時間結果)
//                // (1) 方法 1: 用 LiveData 更新
//                // (2) 方法 2: 從這裡回到 PlanDailyFragment，或是回到 MainActivity > MainPresenter > PlanDailyFragment 更新
//                // *(3) 方法 3: [TODO] 把 TaskDefineTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 ColorDefineTable 物件
//                getColorList();
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


    // [TODO] 未來進階版也許可以上傳自己的 color (但應該先做從 firebase 上更新圖示和圖片名稱這步)
//    @Override
//    public void showCategoryListDialog() {
//        mTaskView.showCategoryListDialog();
//    }


//    public String getStrCurTaskPage() {
//        return mStrCurTaskPage;
//    }
//
//    public void setStrCurTaskPage(String strCurTaskPage) {
//        mStrCurTaskPage = strCurTaskPage;
//    }

}
