package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.object.TimePlanningTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

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

            List<TimePlanningTable> planningTableList = dao.getAllPlanList();

            Logger.d(Constants.TAG, MSG + "Prepare sample plan");
            for (int i = 0 ; i < planningTableList.size() ; ++i) {
                planningTableList.get(i).LogD();
            }

            bean = dao.getTaskListWithPlanTime(mMode, mStrStartTime, mStrEndTime);


//

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

            Logger.d(Constants.TAG, MSG + "GetTaskWithPlanTime success: bean.size() = " + bean.size());
            for (int i = 0 ; i < bean.size() ; ++i) {
                bean.get(i).LogD();
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
