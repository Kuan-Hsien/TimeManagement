package com.kuanhsien.timemanagement.dml;

import android.os.AsyncTask;

import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import java.util.List;

/**
 * Created by Ken on 2018/9/24.
 */
public class GetTaskWithPlanTimeAsyncTask extends AsyncTask<Object, Void, List<GetTaskWithPlanTime>> {

    private static final String MSG = "GetTasksWithPlanAsync:";
    private GetTaskWithPlanTimeCallback mCallback;

    private String mErrorMessage;

    private String mMode;
    private String mStrStartTime;
    private String mStrEndTime;


    public GetTaskWithPlanTimeAsyncTask(String mode, String strStartTime, String strEndTime,
                                        GetTaskWithPlanTimeCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mMode = mode;
        mStrStartTime = strStartTime;
        mStrEndTime = strEndTime;
    }


    @Override
    protected List<GetTaskWithPlanTime> doInBackground(Object[] objects) {

        List<GetTaskWithPlanTime> bean = null;

//        try {

            DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();
            bean = dao.getTaskListWithPlanTime(mMode, mStrStartTime, mStrEndTime);

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
    protected void onPostExecute(List<GetTaskWithPlanTime> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "GetTaskWithPlanTime success");
            for (int i = 0 ; i < bean.size() ; ++i) {
                Logger.d(Constants.TAG, MSG + bean.get(i).getTaskName() + " " );
            }

            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "GetTaskWithPlanTime error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "GetTaskWithPlanTime fail");
        }
    }
}
