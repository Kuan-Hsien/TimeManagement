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
public class SetRecordAsyncTask extends AsyncTask<Object, Void, List<TimeTracingTable>> {

    private static final String MSG = "SetRecordAsyncTask:";
    private SetRecordCallback mCallback;

    private String mErrorMessage;
    private List<TimeTracingTable> mRecordList;


    public SetRecordAsyncTask(List<TimeTracingTable> recordList, SetRecordCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mRecordList = recordList;
    }

    @Override
    protected List<TimeTracingTable> doInBackground(Object[] objects) {

//        try {

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        // edit and add record
        for (int i = 0 ; i < mRecordList.size() ; ++i) {
            dao.addTraceItem(mRecordList.get(i));
        }

        //[TODO] delete log
        Logger.d(Constants.TAG, MSG + "Trace List: ");
        List<TimeTracingTable> recordList = dao.getAllTraceList();
        // edit and add record
        for (int i = 0 ; i < recordList.size() ; ++i) {
            recordList.get(i).LogD();
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

        return mRecordList;
    }

    @Override
    protected void onPostExecute(List<TimeTracingTable> bean) {
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
