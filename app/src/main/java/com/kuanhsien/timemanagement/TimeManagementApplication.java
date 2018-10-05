package com.kuanhsien.timemanagement;

import android.app.Application;
import android.graphics.drawable.Drawable;

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



    public static Drawable getIconResource(String strIcon) {

        if (strIcon.equals("icon_sleep")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_sleep);
        } else if (strIcon.equals("icon_bike")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_bike);
        } else if (strIcon.equals("icon_book")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_book);
        } else if (strIcon.equals("icon_car")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_car);
        } else if (strIcon.equals("icon_computer")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_computer);
        } else if (strIcon.equals("icon_drunk")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_drunk);
        } else if (strIcon.equals("icon_friend")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_friend);
        } else if (strIcon.equals("icon_food")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_food);
        } else if (strIcon.equals("icon_home")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_home);
        } else if (strIcon.equals("icon_lover")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_lover);
        } else if (strIcon.equals("icon_music")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_music);
        } else if (strIcon.equals("icon_paw")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_paw);
        } else if (strIcon.equals("icon_phonecall")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_phonecall);
        } else if (strIcon.equals("icon_swim")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_swim);
        } else if (strIcon.equals("icon_walk")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_walk);
        } else if (strIcon.equals("icon_work")) {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_work);
        } else {
            return TimeManagementApplication.getAppContext().getDrawable(R.drawable.icon_sleep);
        }
    }
}
