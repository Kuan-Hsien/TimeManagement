package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;



/**
 * Created by Ken on 2018/10/09
 */
public class GetTraceDailySummaryAsyncTask extends AsyncTask<Object, Void, List<GetTraceDetail>> {

    private static final String MSG = "GetTraceDailySummaryAsyncTask:";
    private GetTraceDailySummaryCallback mCallback;

    private String mErrorMessage;

    private String mMode;
    private String mStartVerNo;
    private String mEndVerNo;
    private String mCategoryList;
    private String mTaskList;


    public GetTraceDailySummaryAsyncTask(String mode, String startVerNo, String endVerNo, String categoryList, String taskList, 
                                         GetTraceDailySummaryCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mMode = mode;
        mStartVerNo = startVerNo;
        mEndVerNo = endVerNo;
        mCategoryList = categoryList;
        mTaskList = taskList;
    }

    @Override
    protected List<GetTraceDetail> doInBackground(Object[] objects) {

//        try {

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        // (1) Query trace summary in a specific period
        Logger.d(Constants.TAG, MSG + "Trace mode detail: " + mMode + " from " + mStartVerNo + " to " + mEndVerNo + " : ");
        List<GetTraceDetail> traceDailySummaryList = dao.getTraceDailySummary(mMode, mStartVerNo, mEndVerNo, mCategoryList, mTaskList);
        // edit and add record
        for (int i = 0 ; i < traceDailySummaryList.size() ; ++i) {
            traceDailySummaryList.get(i).LogD();
        }

        // [TODO] add exception handling
//        } catch (VoyageInvalidTokenException e) {
//            mErrorMessage = e.getMessage();
//            e.printStackTrace();
//        } catch (VoyageException e) {
//            mErrorMessage = e.getMessage();
//            e.printStackTrace();
//        } catch (IOException e) {
//            mErrorMessage = e.getMessage();
//            e.printStackTrace();
//        }

        return traceDailySummaryList;
    }

    @Override
    protected void onPostExecute(List<GetTraceDetail> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "GetTraceDailySummaryAsyncTask success");
            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "GetTraceDailySummaryAsyncTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "GetTraceDailySummaryAsyncTask fail");
        }
    }
}