package com.kuanhsien.timemanagement.dml;

import android.os.AsyncTask;

import com.kuanhsien.timemanagement.TimeManagementApplication;
import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.object.CategoryDefineTable;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;

import java.util.List;


/**
 * Created by Ken on 2018/10/01
 */
public class SetCategoryAsyncTask extends AsyncTask<Object, Void, List<CategoryDefineTable>> {

    private static final String MSG = "SetCategoryAsyncCategory: ";
    private SetCategoryCallback mCallback;

    private String mErrorMessage;
    private List<CategoryDefineTable> mCategoryList, mDeleteCategoryList;


    public SetCategoryAsyncTask(List<CategoryDefineTable> categoryList, List<CategoryDefineTable> deleteList, SetCategoryCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mCategoryList = categoryList;
        mDeleteCategoryList = deleteList;
    }

    @Override
    protected List<CategoryDefineTable> doInBackground(Object[] objects) {

//        try {

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        // delete category
        dao.deleteCategoryList(mDeleteCategoryList);

        // edit and add category
        for (int i = 0 ; i < mCategoryList.size() ; ++i) {
            dao.addCategory(mCategoryList.get(i));
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