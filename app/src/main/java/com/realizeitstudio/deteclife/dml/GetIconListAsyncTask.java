package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.object.IconDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;


/**
 * Created by Ken on 2018/10/18.
 */
public class GetIconListAsyncTask extends AsyncTask<Object, Void, List<IconDefineTable>> {

    private static final String MSG = "GetIconListAsyncTask:";
    private GetIconListCallback mCallback;

    private String mErrorMessage;


    public GetIconListAsyncTask(GetIconListCallback callback) {
        mCallback = callback;
        mErrorMessage = "";
    }

    @Override
    protected List<IconDefineTable> doInBackground(Object[] objects) {

        List<IconDefineTable> bean = null;

//        try {
        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();
        bean = dao.getAllIconList();

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
    protected void onPostExecute(List<IconDefineTable> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "IconDefineTable success, bean.size() = " + bean.size());
            for (int i = 0 ; i < bean.size() ; ++i) {
                bean.get(i).LogD();
            }

            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "IconDefineTable error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "IconDefineTable fail");
        }
    }
}