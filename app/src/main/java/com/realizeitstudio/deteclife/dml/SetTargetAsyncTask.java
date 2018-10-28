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
 * Created by Ken on 2018/9/25
 */
public class SetTargetAsyncTask extends AsyncTask<Object, Void, List<TimePlanningTable>> {

    private static final String MSG = "SetTargetAsyncTask:";
    private SetTargetCallback mCallback;

    private String mErrorMessage;
    private List<TimePlanningTable> mTargetList;
    private List<TimePlanningTable> mDeleteTargetList;


    public SetTargetAsyncTask(List<TimePlanningTable> targetList, List<TimePlanningTable> deleteList, SetTargetCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mTargetList = targetList;
        mDeleteTargetList = deleteList;
    }

    @Override
    protected List<TimePlanningTable> doInBackground(Object[] objects) {

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        // delete target
        dao.deleteTargetList(mDeleteTargetList);

        // edit and add target
        for (int i = 0; i < mTargetList.size(); ++i) {
            dao.addPlanItem(mTargetList.get(i));
        }

        return mTargetList;
    }

    @Override
    protected void onPostExecute(List<TimePlanningTable> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "SetTargetAsyncTask success");
            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "SetTargetAsyncTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "SetTargetAsyncTask fail");
        }
    }
}
