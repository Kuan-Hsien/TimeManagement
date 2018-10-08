package com.kuanhsien.timemanagement.dml;

import android.os.AsyncTask;

import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.object.TimeTracingTable;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;



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

//        try {
        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();
        bean = dao.getCurrentTraceTask(mStrVerNo);

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

        return bean;
    }

    @Override
    protected void onPostExecute(TimeTracingTable bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "GetCurrentTraceTask success." );
            bean.LogD();

            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "GetCurrentTraceTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "GetCurrentTraceTask fail");
        }
    }
}