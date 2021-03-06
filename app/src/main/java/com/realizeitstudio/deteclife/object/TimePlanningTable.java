package com.realizeitstudio.deteclife.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

/**
 * Created by Ken on 2018/9/24
 */
@Entity(tableName = "time_planning_table",      // table-name (case-sensitive)
        primaryKeys = {"mode", "category_name", "task_name", "start_time"})
public class TimePlanningTable {

    @NonNull
    @ColumnInfo(name = "mode")
    private String mMode;

    @NonNull
    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @NonNull
    @ColumnInfo(name = "task_name")
    private String mTaskName;

    @NonNull
    @ColumnInfo(name = "start_time")
    private String mStartTime;

    @NonNull
    @ColumnInfo(name = "end_time")
    private String mEndTime;

    @ColumnInfo(name = "cost_time")
    private long mCostTime;

    @ColumnInfo(name = "update_date")
    private String mUpdateDate;


//    @ColumnInfo(name = "category_priority")
//    private String categoryPriority;

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.

    public String getMode() {
        return mMode;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public long getCostTime() {
        return mCostTime;
    }

    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setMode(String mode) {
        mMode = mode;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
    }

    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }

    public void setCostTime(long costTime) {
        mCostTime = costTime;
    }

    public void setUpdateDate(String updateDate) {
        mUpdateDate = updateDate;
    }

    @Ignore
    public TimePlanningTable() {

    }

    @Ignore
    // no update_date constructor for delete data
    public TimePlanningTable(@NonNull String mode, @NonNull String categoryName, @NonNull String taskName, @NonNull String startTime, @NonNull String endTime, long costTime) {
        mMode = mode;
        mCategoryName = categoryName;
        mTaskName = taskName;
        mStartTime = startTime;
        mEndTime = endTime;
        mCostTime = costTime;
    }

    public TimePlanningTable(@NonNull String mode, @NonNull String categoryName, @NonNull String taskName, @NonNull String startTime, @NonNull String endTime, long costTime, String updateDate) {
        mMode = mode;
        mCategoryName = categoryName;
        mTaskName = taskName;
        mStartTime = startTime;
        mEndTime = endTime;
        mCostTime = costTime;
        mUpdateDate = updateDate;
    }

    private static final String MSG = "TimePlanningTable: ";

    public void logD() {
        Logger.d(Constants.TAG, MSG + "--------------------- Target -------------------------");
        Logger.d(Constants.TAG, MSG + "Mode: " + getMode());
        Logger.d(Constants.TAG, MSG + "CategoryName: " + getCategoryName());
        Logger.d(Constants.TAG, MSG + "TaskName: " + getTaskName());
        Logger.d(Constants.TAG, MSG + "StartTime: " + getStartTime());
        Logger.d(Constants.TAG, MSG + "EndTime: " + getEndTime());
        Logger.d(Constants.TAG, MSG + "CostTime: " + getCostTime());
        Logger.d(Constants.TAG, MSG + "-----------------------------------------------------");
    }
}
