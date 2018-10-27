package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;


/**
 * Created by Ken on 2018/10/07
 */
public class SetRecordAsyncTask extends AsyncTask<Object, Void, List<GetTraceSummary>> {

    private static final String MSG = "SetRecordAsyncTask:";
    private SetRecordCallback mCallback;

    private String mErrorMessage;

    private List<TimeTracingTable> mRecordList;
    private String mStartVerNo;
    private String mEndVerNo;
    private String mCategoryList;
    private String mTaskList;


    public SetRecordAsyncTask(List<TimeTracingTable> recordList,
                              String startVerNo, String endVerNo, String categoryList, String taskList,
                              SetRecordCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mRecordList = recordList;
        mStartVerNo = startVerNo;
        mEndVerNo = endVerNo;
        mCategoryList = categoryList;
        mTaskList = taskList;
    }

    @Override
    protected List<GetTraceSummary> doInBackground(Object[] objects) {

//        try {

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        // (1) insert trace results and new-start task
        for (int i = 0; i < mRecordList.size(); ++i) {
            dao.addTraceItem(mRecordList.get(i));
        }

        // (2) Query trace summary in a specific period
        Logger.d(Constants.TAG, MSG + "Trace summary from " + mStartVerNo + " to " + mEndVerNo + " : ");
        List<GetTraceSummary> traceSummaryList = dao.getTraceSummary(mStartVerNo, mEndVerNo, mCategoryList, mTaskList);
        // edit and add record
        for (int i = 0; i < traceSummaryList.size(); ++i) {
            traceSummaryList.get(i).logD();
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

        return traceSummaryList;
    }

    @Override
    protected void onPostExecute(List<GetTraceSummary> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "SetRecordAsyncTask success");
            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "SetRecordAsyncTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "SetRecordAsyncTask fail");
        }
    }
}
