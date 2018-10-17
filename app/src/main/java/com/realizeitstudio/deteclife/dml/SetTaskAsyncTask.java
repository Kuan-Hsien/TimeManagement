package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;


/**
 * Created by Ken on 2018/10/01
 */
public class SetTaskAsyncTask extends AsyncTask<Object, Void, List<TaskDefineTable>> {

    private static final String MSG = "SetTaskAsyncTask: ";
    private SetTaskCallback mCallback;

    private String mErrorMessage;
    private List<TaskDefineTable> mTaskList, mDeleteTaskList;


    public SetTaskAsyncTask(List<TaskDefineTable> taskList, List<TaskDefineTable> deleteList, SetTaskCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mTaskList = taskList;
        mDeleteTaskList = deleteList;
    }

    @Override
    protected List<TaskDefineTable> doInBackground(Object[] objects) {

//        try {

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        // delete task
        dao.deleteTaskList(mDeleteTaskList);

        // edit and add task
        for (int i = 0 ; i < mTaskList.size() ; ++i) {
            dao.addTask(mTaskList.get(i));
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

        return mTaskList;
    }

    @Override
    protected void onPostExecute(List<TaskDefineTable> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "SetTaskAsyncTask success");
            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "SetTaskAsyncTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "SetTaskAsyncTask fail");
        }
    }
}