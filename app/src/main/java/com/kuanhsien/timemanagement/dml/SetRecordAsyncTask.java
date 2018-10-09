package com.kuanhsien.timemanagement.dml;

import android.os.AsyncTask;

import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.object.TimeTracingTable;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;

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
        for (int i = 0 ; i < mRecordList.size() ; ++i) {
            dao.addTraceItem(mRecordList.get(i));
        }

        // (2) Query trace summary in a specific period
        Logger.d(Constants.TAG, MSG + "Trace summary from " + mStartVerNo + " to " + mEndVerNo + " : ");
        List<GetTraceSummary> traceSummaryList = dao.getTraceSummary(mStartVerNo, mEndVerNo, mCategoryList, mTaskList);
        // edit and add record
        for (int i = 0 ; i < traceSummaryList.size() ; ++i) {
            traceSummaryList.get(i).LogD();
        }


//        insert a new record
//        dao.addTraceItem(new TimeTracingTable(Constants.MODE_PERIOD, "HEALTH", "Sleep", mStrStartTime, mStrEndTime, mStrCostTime));
//        dao.addTraceItem(new TimeTracingTable(Constants.MODE_PERIOD, "HEALTH", "Eat", mStrStartTime, mStrEndTime, mStrCostTime));
//        dao.addTraceItem(new TimeTracingTable(Constants.MODE_PERIOD, "RELATIONSHIP", "Family", mStrStartTime, mStrEndTime, mStrCostTime));
//        dao.addTraceItem(new TimeTracingTable(Constants.MODE_PERIOD, "RELATIONSHIP", "Family", mStrStartTime, mStrEndTime, mStrCostTime));
//        dao.addTraceItem(new TimeTracingTable(Constants.MODE_PERIOD, "HEALTH", "Toilet", mStrStartTime, mStrEndTime, mStrCostTime));

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
