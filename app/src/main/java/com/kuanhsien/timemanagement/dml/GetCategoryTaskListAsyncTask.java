package com.kuanhsien.timemanagement.dml;

import android.os.AsyncTask;

import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import java.util.List;

/**
 * Created by Ken on 2018/9/30.
 */
public class GetCategoryTaskListAsyncTask extends AsyncTask<Object, Void, List<GetCategoryTaskList>> {

    private static final String MSG = "GetCategoryTaskListAsync:";
    private GetCategoryTaskListCallback mCallback;

    private String mErrorMessage;


    public GetCategoryTaskListAsyncTask(GetCategoryTaskListCallback callback) {
        mCallback = callback;
        mErrorMessage = "";
    }

    @Override
    protected List<GetCategoryTaskList> doInBackground(Object[] objects) {

        List<GetCategoryTaskList> bean = null;

//        try {
        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();
        bean = dao.getCategoryTaskList();

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
    protected void onPostExecute(List<GetCategoryTaskList> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "GetCategoryTaskList success");
            for (int i = 0 ; i < bean.size() ; ++i) {
                Logger.d(Constants.TAG, MSG + bean.get(i).getTaskName() + " " );
            }

            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "GetCategoryTaskList error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "GetCategoryTaskList fail");
        }
    }
}

