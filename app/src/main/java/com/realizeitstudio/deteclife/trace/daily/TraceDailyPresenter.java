package com.realizeitstudio.deteclife.trace.daily;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTimeCallback;
import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTimeAsyncTask;
import com.realizeitstudio.deteclife.dml.SetTargetAsyncTask;
import com.realizeitstudio.deteclife.dml.SetTargetCallback;
import com.realizeitstudio.deteclife.dml.GetTaskWithPlanTime;
import com.realizeitstudio.deteclife.object.TimePlanningTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Ken on 2018/10/02.
 */
public class TraceDailyPresenter implements TraceDailyContract.Presenter {
    private static final String MSG = "TraceDailyPresenter: ";

    private final TraceDailyContract.View mTraceView;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;


    public TraceDailyPresenter(TraceDailyContract.View mainView) {
        mTraceView = checkNotNull(mainView, "traceView cannot be null!");
        mTraceView.setPresenter(this);
    }

    @Override
    public void start() {
//        getTaskWithTraceTime();
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
        mTraceView.refreshUi(mode);
    }


    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getTaskWithTraceTime() {
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
            String mStrStartTime = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO).format(currentTime); // 擷取到日期

            // 新增一個Calendar,並且指定時間
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.HOUR, 24);    // +24 小時

            Date tomorrowNow = calendar.getTime();  // 取得 24 小時後的現在時間
            String mStrEndTime = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO).format(tomorrowNow);   // 擷取到日期


            new GetTaskWithPlanTimeAsyncTask(
                    Constants.MODE_DAILY, mStrStartTime, mStrEndTime, new GetTaskWithPlanTimeCallback() {

                @Override
                public void onCompleted(List<GetTaskWithPlanTime> bean) {
                    setLoading(false);
                    showTaskListWithTraceTime(bean);
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
    public void showTaskListWithTraceTime(List<GetTaskWithPlanTime> bean) {
        mTraceView.showTaskListWithTraceTime(bean);
    }


    // 2-1. [Send-to-Model] database insert to update data (insert new targets or adjust time for existed targets)
    @Override
    public void saveTargetResults(List<TimePlanningTable> targetList, List<TimePlanningTable> deleteTargetList) {
//    public void saveTargetResults(String strMode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime) {

        // insert time_tracening_table
        new SetTargetAsyncTask(targetList, deleteTargetList,  new SetTargetCallback() {

            @Override
            public void onCompleted(List<TimePlanningTable> bean) {

                Logger.d(Constants.TAG, MSG + "SetTarget onCompleted");
                for( int i = 0 ; i < bean.size() ; ++i) {
                    bean.get(i).LogD();
                }

                // [TODO] insert 資料後更新畫面，目前是將要更新的資料全部當作 bean
                // 假如有順利 insert，則跳回 Trace Fragment，但是裡面的內容要更新 (重新撈取資料或是把所有更新項目都塞進 list 中，也包含 edit 的時間結果)
                // (1) 方法 1: 用 LiveData 更新
                // (2) 方法 2: 從這裡回到 TraceDailyFragment，或是回到 MainActivity > MainPresenter > TraceDailyFragment 更新
                // *(3) 方法 3: [TODO] 把 TimePlanningTable 中增加 icon 和 color，就可以直接把這個物件當作畫面要顯示的內容。而不用另外再做一次畫面。也不用另外寫 GetTaskWithPlanTime 物件
                getTaskWithTraceTime();
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
        mTraceView.showSetTargetUi();
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    @Override
    public void showTaskListDialog() {
        mTraceView.showTaskListDialog();
    }
}