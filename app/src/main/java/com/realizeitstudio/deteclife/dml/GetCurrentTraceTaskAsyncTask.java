package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.object.TimeTracingTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;



/**
 * Created by Ken on 2018/10/07.
 */
public class GetCurrentTraceTaskAsyncTask extends AsyncTask<Object, Void, TimeTracingTable> {

    private static final String MSG = "GetCurrentTraceTaskAsync:";
    private GetCurrentTraceTaskCallback mCallback;

    private String mErrorMessage;
    private String mStrVerNo;


    public GetCurrentTraceTaskAsyncTask(String strVerNo, GetCurrentTraceTaskCallback callback) {
        mCallback = callback;
        mErrorMessage = "";
        mStrVerNo = strVerNo;
    }

    @Override
    protected TimeTracingTable doInBackground(Object[] objects) {

        TimeTracingTable bean = null;

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();
        bean = dao.getCurrentTraceTask(mStrVerNo);

        return bean;
    }

    @Override
    protected void onPostExecute(TimeTracingTable bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "GetCurrentTraceTask success.");
            bean.logD();

            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "GetCurrentTraceTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "GetCurrentTraceTask fail");
        }
    }
}