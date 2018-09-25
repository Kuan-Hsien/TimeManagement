package com.kuanhsien.timemanagement;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by Ken on 2018/9/24
 */
public class GetTaskWithPlanTime {

    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @ColumnInfo(name = "task_name")
    private String mTaskName;

    @ColumnInfo(name = "task_color")
    private String mTaskColor;

    @ColumnInfo(name = "task_icon")
    private String mTaskIcon;

    @ColumnInfo(name = "cost_time")
    private String mCostTime;

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

    public String getCostTime() {
        return mCostTime;
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

    public GetTaskWithPlanTime() {
    }
}
