package com.realizeitstudio.deteclife.analysis.weekly;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.realizeitstudio.deteclife.dml.GetCurrentTraceTaskAsyncTask;
import com.realizeitstudio.deteclife.dml.GetCurrentTraceTaskCallback;
import com.realizeitstudio.deteclife.dml.GetResultDailySummary;
import com.realizeitstudio.deteclife.dml.GetResultDailySummaryAsyncTask;
import com.realizeitstudio.deteclife.dml.GetResultDailySummaryCallback;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;
import com.realizeitstudio.deteclife.utils.ParseTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ken on 2018/10/12.
 */
public class AnalysisWeeklyPresenter implements AnalysisWeeklyContract.Presenter {
    private static final String MSG = "AnalysisWeeklyPresenter: ";

    private final AnalysisWeeklyContract.View mAnalysisView;

    private int mlastVisibleItemPosition;
    private int mfirstVisibleItemPosition;

    private boolean mLoading = false;
    private boolean mLoadingCurrentTracing = false;


    public AnalysisWeeklyPresenter(AnalysisWeeklyContract.View mainView) {
        mAnalysisView = checkNotNull(mainView, "analysisView cannot be null!");
        mAnalysisView.setPresenter(this);
    }

    @Override
    public void start() {

        // (1) Statistics List
        getResultWeeklySummary();

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
        mAnalysisView.refreshUi(mode);
    }


    // 1-1. [Send-to-Model] database query to prepare data (query all targets)
    @Override
    public void getResultWeeklySummary() {
        if (!isLoading()) {
            setLoading(true);

//        // 取得現在時間
//        Date curDate = new Date();
//        // 定義時間格式
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh24mm");
//        // 透過SimpleDateFormat的format方法將 Date 轉為字串
//        String strCurrentTime = simpleDateFormat.format(curDate);


//            // 取得現在時間
//            Date currentTime = new Date();
//            String mStrStartTime = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO).format(currentTime); // 擷取到日期
//
//            // 新增一個Calendar,並且指定時間
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(currentTime);
//            calendar.add(Calendar.HOUR, 24);    // +24 小時
//
//            Date tomorrowNow = calendar.getTime();  // 取得 24 小時後的現在時間
//            String mStrEndTime = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO).format(tomorrowNow);   // 擷取到日期


            // （Method-1) 昨天日期
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, -1);
//        Date date = calendar.getTime();
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String lastVerNo = simpleDateFormat.format(date);

            // （Method-2) 今天日期 [TODO] 改為用畫面上的元件讀取
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DB_FORMAT_VER_NO);

            // 找出昨天日期，當作 endVerNo 傳入，以撈取昨天統計資料
            // Weekly 的統計中，暫定是星期一到星期天為一週
            //  如果昨天是星期一，則 Weekly 也只撈一天 (beginVerNo = endVerNo)
            //  如果昨天是星期二，則 Weekly 撈兩天 (beginVerNo = endVerNo - 1)

            // 昨天日期
//            Date date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);

            // 今天日期
            Date date = new Date();
            String endVerNo = simpleDateFormat.format(date);

            int intWeekDay = ParseTime.date2Day(date);    // 把今天傳入，回傳星期幾 (1 = 星期一，2 = 星期二)

            // 計算 Weekly 的開始時間 (beginVerNo)
            date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (intWeekDay - 1)); // 如果今天是星期一，則 Weekly 也只撈一天 (beginVerNo = endVerNo)，和 endVerNo 一樣只往回減一天
            String beginVerNo = simpleDateFormat.format(date);


            // get weekly summary of tracing and planning result
            new GetResultDailySummaryAsyncTask("ALL", beginVerNo, endVerNo, "ALL", "ALL", new GetResultDailySummaryCallback() {

                @Override
                public void onCompleted(List<GetResultDailySummary> bean) {

                    Logger.d(Constants.TAG, MSG + "GetResultDailySummary onCompleted: bean.size() = " + bean.size());

                    List<GetResultDailySummary> dailySummaryList = new ArrayList<>();
                    List<GetResultDailySummary> weeklySummaryList = new ArrayList<>();

                    for (int i = 0; i < bean.size(); ++i) {
                        bean.get(i).logD();

                        // 分別存成 daily 和 weekly 的結果，TODO 放進兩個不同的 adapter 中，甚至一次撈一整週
                        if (Constants.MODE_DAILY.equals(bean.get(i).getMode())) {     // Daily summary

                            dailySummaryList.add(bean.get(i));

                        } else {    // Constants.MODE_WEEKLY.equals(bean.get(i).getMode())  // Weekly summary

                            weeklySummaryList.add(bean.get(i));
                        }
                    }

                    showResultWeeklySummary(weeklySummaryList);
                    // TODO add weekly
//                    showResultWeeklySummary(weeklySummaryList);

                    setLoading(false);
                }

                @Override
                public void onError(String errorMessage) {

                    setLoading(false);
                    Logger.e(Constants.TAG, MSG + "GetResultWeeklySummary onError, errorMessage: " + errorMessage);

                }
            }).execute();

        }
    }


    // 1-2. [Send-to-View] request fragment to show data
    @Override
    public void showResultWeeklySummary(List<GetResultDailySummary> bean) {
        mAnalysisView.showResultDailySummary(bean);
    }


    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }


    /////////////////////////////
    // 1-1. [Send-to-Model] database query to prepare data (query current record item)
    @Override
    public void getCurrentTraceItem(String strVerNo) {

        Logger.d(Constants.TAG, MSG + "getCurrentTraceItem: VerNo: " + strVerNo);

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
        mAnalysisView.showCurrentTraceItem(bean);
    }


    public boolean isLoadingCurrentTracing() {
        return mLoadingCurrentTracing;
    }

    public void setLoadingCurrentTracing(boolean loadingCurrentTracing) {
        mLoadingCurrentTracing = loadingCurrentTracing;
    }
}
