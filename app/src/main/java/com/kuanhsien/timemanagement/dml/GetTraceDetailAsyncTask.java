package com.kuanhsien.timemanagement.dml;

import android.os.AsyncTask;

import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;

import java.util.List;

/**
 * Created by Ken on 2018/10/09
 */
public class GetTraceDetailAsyncTask extends AsyncTask<Object, Void, List<GetTraceDetail>> {

    private static final String MSG = "GetTraceDetailAsyncTask:";
    private GetTraceDetailCallback mCallback;

    private String mErrorMessage;

    private String mMode;
    private String mStartVerNo;
    private String mEndVerNo;
    private String mCategoryList;
    private String mTaskList;


    public GetTraceDetailAsyncTask(String mode, String startVerNo, String endVerNo, String categoryList, String taskList,
                                   GetTraceDetailCallback callback) {
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
        List<GetTraceDetail> traceDetailList = dao.getTraceDetail(mMode, mStartVerNo, mEndVerNo, mCategoryList, mTaskList);
        // edit and add record
        for (int i = 0 ; i < traceDetailList.size() ; ++i) {
            traceDetailList.get(i).LogD();
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

        return traceDetailList;
    }

    @Override
    protected void onPostExecute(List<GetTraceDetail> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "GetTraceDetailAsyncTask success");
            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "GetTraceDetailAsyncTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "GetTraceDetailAsyncTask fail");
        }
    }
}