package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.object.ColorDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;

/**
 * Created by Ken on 2018/10/27.
 */
public class GetColorListAsyncTask extends AsyncTask<Object, Void, List<ColorDefineTable>> {

    private static final String MSG = "GetColorListAsyncTask:";
    private GetColorListCallback mCallback;

    private String mErrorMessage;


    public GetColorListAsyncTask(GetColorListCallback callback) {
        mCallback = callback;
        mErrorMessage = "";
    }

    @Override
    protected List<ColorDefineTable> doInBackground(Object[] objects) {

        List<ColorDefineTable> bean = null;

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();
        bean = dao.getAllColorList();

        return bean;
    }

    @Override
    protected void onPostExecute(List<ColorDefineTable> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "ColorDefineTable success, bean.size() = " + bean.size());
            for (int i = 0; i < bean.size(); ++i) {
                bean.get(i).logD();
            }

            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "ColorDefineTable error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "ColorDefineTable fail");
        }
    }
}