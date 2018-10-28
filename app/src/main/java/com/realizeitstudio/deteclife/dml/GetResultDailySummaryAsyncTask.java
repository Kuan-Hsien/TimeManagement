package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;


/**
 * Created by Ken on 2018/10/10
 */
public class GetResultDailySummaryAsyncTask extends AsyncTask<Object, Void, List<GetResultDailySummary>> {

    private static final String MSG = "GetResultDailySummaryAsyncTask:";
    private GetResultDailySummaryCallback mCallback;

    private String mErrorMessage;

    private String mMode;
    private String mStartVerNo;
    private String mEndVerNo;
    private String mCategoryList;
    private String mTaskList;


    public GetResultDailySummaryAsyncTask(String mode, String startVerNo, String endVerNo, String categoryList, String taskList,
                                         GetResultDailySummaryCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mMode = mode;
        mStartVerNo = startVerNo;
        mEndVerNo = endVerNo;
        mCategoryList = categoryList;
        mTaskList = taskList;
    }

    @Override
    protected List<GetResultDailySummary> doInBackground(Object[] objects) {

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        // (1) Query trace summary in a specific period
        Logger.d(Constants.TAG, MSG + "Reuslt daily summary: Mode: " + mMode + " From " + mStartVerNo + " To " + mEndVerNo + " : ");
        List<GetResultDailySummary> resultDailySummaryList = dao.getResultDailySummary(mMode, mStartVerNo, mEndVerNo, mCategoryList, mTaskList);
        Logger.d(Constants.TAG, MSG + "resultDailySummaryList.size() = " + resultDailySummaryList.size());
        // edit and add record
        for (int i = 0; i < resultDailySummaryList.size(); ++i) {
            resultDailySummaryList.get(i).logD();
        }

        return resultDailySummaryList;
    }

    @Override
    protected void onPostExecute(List<GetResultDailySummary> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "GetResultDailySummaryAsyncTask success: bean.size() = " + bean.size());
            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "GetResultDailySummaryAsyncTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "GetResultDailySummaryAsyncTask fail");
        }
    }
}