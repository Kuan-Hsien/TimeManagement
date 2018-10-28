package com.realizeitstudio.deteclife.dml;

import android.os.AsyncTask;

import com.realizeitstudio.deteclife.TimeManagementApplication;
import com.realizeitstudio.deteclife.database.AppDatabase;
import com.realizeitstudio.deteclife.database.DatabaseDao;
import com.realizeitstudio.deteclife.object.CategoryDefineTable;
import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

import java.util.List;


/**
 * Created by Ken on 2018/10/01
 */
public class SetCategoryAsyncTask extends AsyncTask<Object, Void, List<CategoryDefineTable>> {

    private static final String MSG = "SetCategoryAsyncCategory: ";
    private SetCategoryCallback mCallback;

    private String mErrorMessage;
    private List<CategoryDefineTable> mCategoryList;
    private List<CategoryDefineTable> mDeleteCategoryList;


    public SetCategoryAsyncTask(List<CategoryDefineTable> categoryList, List<CategoryDefineTable> deleteList, SetCategoryCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mCategoryList = categoryList;
        mDeleteCategoryList = deleteList;
    }

    @Override
    protected List<CategoryDefineTable> doInBackground(Object[] objects) {

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        // delete category
        dao.deleteCategoryList(mDeleteCategoryList);

        // edit and add category
        for (int i = 0; i < mCategoryList.size(); ++i) {
            dao.addCategory(mCategoryList.get(i));
        }

        return mCategoryList;
    }

    @Override
    protected void onPostExecute(List<CategoryDefineTable> bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "SetCategoryAsyncCategory success");
            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "SetCategoryAsyncCategory error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "SetCategoryAsyncCategory fail");
        }
    }
}