package com.kuanhsien.timemanagement;

import android.os.AsyncTask;

import com.kuanhsien.timemanagement.database.AppDatabase;
import com.kuanhsien.timemanagement.database.DatabaseDao;
import com.kuanhsien.timemanagement.object.TaskDefineTable;
import com.kuanhsien.timemanagement.object.TimePlanningTable;
import com.kuanhsien.timemanagement.utli.Constants;
import com.kuanhsien.timemanagement.utli.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Ken on 2018/9/25
 */
public class SetTargetAsyncTask extends AsyncTask<Object, Void, TimePlanningTable> {

    private static final String MSG = "SetTargetAsyncTask:";
    private SetTargetCallback mCallback;

    private String mErrorMessage;

    private String mMode;
    private String mStrCategory;
    private String mStrTask;
    private String mStrStartTime;
    private String mStrEndTime;
    private String mStrCostTime;


    public SetTargetAsyncTask(String mode, String strCategory, String strTask, String strStartTime, String strEndTime, String strCostTime,
                              SetTargetCallback callback) {
        mCallback = callback;
        mErrorMessage = "";

        mMode = mode;
        mStrCategory = strCategory;
        mStrTask = strTask;
        mStrStartTime = strStartTime;
        mStrEndTime = strEndTime;
        mStrCostTime = strCostTime;
    }

    @Override
    protected TimePlanningTable doInBackground(Object[] objects) {

        TimePlanningTable bean = null;

//        try {

        // update_date
        // 取得現在時間
        Date curDate = new Date();
        // 定義時間格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        // 透過SimpleDateFormat的format方法將 Date 轉為字串
        String strCurrentTime = simpleDateFormat.format(curDate);

        DatabaseDao dao = AppDatabase.getDatabase(TimeManagementApplication.getAppContext()).getDatabaseDao();

        bean = new TimePlanningTable(Constants.MODE_PERIOD, mStrCategory, mStrTask, mStrStartTime, mStrEndTime, mStrCostTime, strCurrentTime);
        dao.addPlanItem(bean);

//        insert a new target
//        dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "HEALTH", "Sleep", mStrStartTime, mStrEndTime, mStrCostTime));
//        dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "HEALTH", "Eat", mStrStartTime, mStrEndTime, mStrCostTime));
//        dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "RELATIONSHIP", "Family", mStrStartTime, mStrEndTime, mStrCostTime));
//        dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "RELATIONSHIP", "Family", mStrStartTime, mStrEndTime, mStrCostTime));
//        dao.addPlanItem(new TimePlanningTable(Constants.MODE_PERIOD, "HEALTH", "Toilet", mStrStartTime, mStrEndTime, mStrCostTime));

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
    protected void onPostExecute(TimePlanningTable bean) {
        super.onPostExecute(bean);

        if (bean != null) {

            Logger.d(Constants.TAG, MSG + "SetTargetAsyncTask success");
9            mCallback.onCompleted(bean);

        } else if (!mErrorMessage.equals("")) {

            Logger.d(Constants.TAG, MSG + "SetTargetAsyncTask error");
            mCallback.onError(mErrorMessage);

        } else {

            Logger.d(Constants.TAG, MSG + "SetTargetAsyncTask fail");
        }
    }
}
