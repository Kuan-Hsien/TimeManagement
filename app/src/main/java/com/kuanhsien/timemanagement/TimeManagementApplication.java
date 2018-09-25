package com.kuanhsien.timemanagement;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.kuanhsien.timemanagement.utli.Constants;

import java.io.File;

/**
 * Created by Ken on 2018/9/24.
 */
public class TimeManagementApplication extends Application {

    private static TimeManagementApplication mContext;
//    public static DbFavoriteArticle dbFavoriteArticle;

//    public static int appModeValue;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

//        dbFavoriteArticle = new DbFavoriteArticle(applicationContext);
//
//        // set APP mode from file in external storage
//        //
//        // read control file from external storage
//        File fileControl = new File(Environment.getExternalStorageDirectory().getPath() + Constants.FILE_PATH_EXTERNAL_STORAGE , Constants.FILENAME_APP_MODE);
//        String strAppMode = null;
//
//        // check if exist External Control File
//        if (fileControl.exists()) {
//            strAppMode = new ExternalStorageHelper().readFile(Constants.FILENAME_APP_MODE);
//        }
//        setAppMode(strAppMode);
    }

    public static TimeManagementApplication getAppContext() {
        return mContext;
    }

//    public static DbFavoriteArticle getDatabase() {
//        return dbFavoriteArticle;
//    }

//    public void setAppMode(String appMode) {
//
//        if (Constants.APP_MODE_DEMO.equals(appMode)) {
//            this.appModeValue = Constants.APP_MODE_VALUE_DEMO;
//
//        } else if (Constants.APP_MODE_DEV.equals(appMode)) {
//            this.appModeValue = Constants.APP_MODE_VALUE_DEV;
//
//        } else {
//            this.appModeValue = Constants.APP_MODE_VALUE_RLS;
//        }
//    }
//
//    public static int getAppMode() {
//        return appModeValue;
//    }

}
