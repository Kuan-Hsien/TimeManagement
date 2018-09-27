package com.kuanhsien.timemanagement;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;

/**
 * Created by Ken on 2018/9/24
 */
public class GetTaskWithPlanTime {

    @ColumnInfo(name = "mode")
    private String mMode;

    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @ColumnInfo(name = "task_name")
    private String mTaskName;

    @ColumnInfo(name = "task_color")
    private String mTaskColor;

    @ColumnInfo(name = "task_icon")
    private String mTaskIcon;

    @ColumnInfo(name = "start_time")
    private String mStartTime;

    @ColumnInfo(name = "end_time")
    private String mEndTime;

    @ColumnInfo(name = "cost_time")
    private String mCostTime;

    public String getMode() {
        return mMode;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public String getTaskColor() {
        return mTaskColor;
    }

    public String getTaskIcon() {
        return mTaskIcon;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public String getCostTime() {
        return mCostTime;
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

    public void setTaskColor(String taskColor) {
        mTaskColor = taskColor;
    }

    public void setTaskIcon(String taskIcon) {
        mTaskIcon = taskIcon;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }

    public void setCostTime(String costTime) {
        mCostTime = costTime;
    }

    public GetTaskWithPlanTime(String categoryName, String taskName, String taskColor, String taskIcon, String costTime) {
        mCategoryName = categoryName;
        mTaskName = taskName;
        mTaskColor = taskColor;
        mTaskIcon = taskIcon;
        mCostTime = costTime;
    }

    public GetTaskWithPlanTime(String mode, String categoryName, String taskName, String taskColor, String taskIcon, String startTime, String endTime, String costTime) {
        mMode = mode;
        mCategoryName = categoryName;
        mTaskName = taskName;
        mTaskColor = taskColor;
        mTaskIcon = taskIcon;
        mStartTime = startTime;
        mEndTime = endTime;
        mCostTime = costTime;
    }

    public GetTaskWithPlanTime() {
    }
}
