package com.realizeitstudio.deteclife.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;


/**
 * Created by Ken on 2018/10/07
 */
@Entity(tableName = "time_tracing_table",      // table-name (case-sensitive)
        primaryKeys = {"ver_no", "category_name", "task_name", "start_time"})
public class TimeTracingTable {

    private static final String MSG = "TimeTracingTable: ";

    @NonNull
    @ColumnInfo(name = "ver_no")
    private String mVerNo;

    @NonNull
    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @NonNull
    @ColumnInfo(name = "task_name")
    private String mTaskName;

    @NonNull
    @ColumnInfo(name = "start_time")
    private Long mStartTime;

    @ColumnInfo(name = "end_time")
    private Long mEndTime;

    @ColumnInfo(name = "cost_time")
    private Long mCostTime;

    @ColumnInfo(name = "update_date")
    private String mUpdateDate;


    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.

    @NonNull
    public String getVerNo() {
        return mVerNo;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public Long getStartTime() {
        return mStartTime;
    }

    public Long getEndTime() {
        return mEndTime;
    }

    public Long getCostTime() {
        return mCostTime;
    }

    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setVerNo(@NonNull String verNo) {
        mVerNo = verNo;
    }

    public void setCategoryName(@NonNull String categoryName) {
        mCategoryName = categoryName;
    }

    public void setTaskName(@NonNull String taskName) {
        mTaskName = taskName;
    }

    public void setStartTime(@NonNull Long startTime) {
        mStartTime = startTime;
    }

    public void setEndTime(Long endTime) {
        mEndTime = endTime;
    }

    public void setCostTime(Long costTime) {
        mCostTime = costTime;
    }

    public void setUpdateDate(String updateDate) {
        mUpdateDate = updateDate;
    }

    public TimeTracingTable() {

    }

    public TimeTracingTable(TimeTracingTable bean) {
        mVerNo = bean.getVerNo();
        mCategoryName = bean.getCategoryName();
        mTaskName = bean.getTaskName();
        mStartTime = bean.getStartTime();
        mEndTime = bean.getEndTime();
        mCostTime = bean.getCostTime();
        mUpdateDate = bean.getUpdateDate();
    }

    public TimeTracingTable(@NonNull String verNo, @NonNull String categoryName, @NonNull String taskName, @NonNull Long startTime, Long endTime, Long costTime) {
        mVerNo = verNo;
        mCategoryName = categoryName;
        mTaskName = taskName;
        mStartTime = startTime;
        mEndTime = endTime;
        mCostTime = costTime;
    }

    public TimeTracingTable(@NonNull String verNo, @NonNull String categoryName, @NonNull String taskName, @NonNull Long startTime, Long endTime, Long costTime, String updateDate) {
        mVerNo = verNo;
        mCategoryName = categoryName;
        mTaskName = taskName;
        mStartTime = startTime;
        mEndTime = endTime;
        mCostTime = costTime;
        mUpdateDate = updateDate;
    }

    public void logD() {
        Logger.d(Constants.TAG, MSG + "--------------------- Trace -------------------------");
        Logger.d(Constants.TAG, MSG + "VerNo: " + getVerNo());
        Logger.d(Constants.TAG, MSG + "CategoryName: " + getCategoryName());
        Logger.d(Constants.TAG, MSG + "TaskName: " + getTaskName());
        Logger.d(Constants.TAG, MSG + "StartTime: " + getStartTime());
        Logger.d(Constants.TAG, MSG + "EndTime: " + getEndTime());
        Logger.d(Constants.TAG, MSG + "CostTime: " + getCostTime());
        Logger.d(Constants.TAG, MSG + "-----------------------------------------------------");
    }
}
