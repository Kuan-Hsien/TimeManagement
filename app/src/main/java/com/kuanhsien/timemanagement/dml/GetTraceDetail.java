package com.kuanhsien.timemanagement.dml;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;

import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;


/**
 * Created by Ken on 2018/10/09
 *
 * Query Result
 */

public class GetTraceDetail {

    @ColumnInfo(name = "mode")
    private String mMode;

    @ColumnInfo(name = "ver_no")
    private String mVerNo;

    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @ColumnInfo(name = "category_color")
    private String mCategoryColor;

    @ColumnInfo(name = "category_priority")
    private int mCategoryPriority;

    @ColumnInfo(name = "task_name")
    private String mTaskName;

    @ColumnInfo(name = "task_color")
    private String mTaskColor;

    @ColumnInfo(name = "task_icon")
    private String mTaskIcon;

    @ColumnInfo(name = "task_priority")
    private int mTaskPriority;

    @ColumnInfo(name = "cost_time")
    private int mCostTime;

    // getter and setter
    public String getMode() {
        return mMode;
    }

    public String getVerNo() {
        return mVerNo;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getCategoryColor() {
        return mCategoryColor;
    }

    public int getCategoryPriority() {
        return mCategoryPriority;
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

    public int getTaskPriority() {
        return mTaskPriority;
    }

    public int getCostTime() {
        return mCostTime;
    }

    public void setMode(String mode) {
        mMode = mode;
    }

    public void setVerNo(String verNo) {
        mVerNo = verNo;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
    }

    public void setCategoryColor(String categoryColor) {
        mCategoryColor = categoryColor;
    }

    public void setCategoryPriority(int categoryPriority) {
        mCategoryPriority = categoryPriority;
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

    public void setTaskPriority(int taskPriority) {
        mTaskPriority = taskPriority;
    }

    public void setCostTime(int costTime) {
        mCostTime = costTime;
    }

    public GetTraceDetail() {
    }

    public GetTraceDetail(String mode, String verNo, String categoryName, String categoryColor, int categoryPriority, String taskName, String taskColor, String taskIcon, int taskPriority, int costTime) {
        mMode = mode;
        mVerNo = verNo;
        mCategoryName = categoryName;
        mCategoryColor = categoryColor;
        mCategoryPriority = categoryPriority;
        mTaskName = taskName;
        mTaskColor = taskColor;
        mTaskIcon = taskIcon;
        mTaskPriority = taskPriority;
        mCostTime = costTime;
    }

    private static final String MSG = "GetTraceDetail: ";

    public void LogD() {
        Logger.d(Constants.TAG, MSG + "------------------- TraceDetail ---------------------");
        Logger.d(Constants.TAG, MSG + "Mode: " + getMode());
        Logger.d(Constants.TAG, MSG + "VerNo: " + getVerNo());
        Logger.d(Constants.TAG, MSG + "CategoryName: " + getCategoryName());
        Logger.d(Constants.TAG, MSG + "CategoryColor: " + getCategoryColor());
        Logger.d(Constants.TAG, MSG + "CategoryPriority: " + getCategoryPriority());
        Logger.d(Constants.TAG, MSG + "TaskName: " + getTaskName());
        Logger.d(Constants.TAG, MSG + "TaskColor: " + getTaskColor());
        Logger.d(Constants.TAG, MSG + "TaskIcon: " + getTaskIcon());
        Logger.d(Constants.TAG, MSG + "TaskPriority: " + getTaskPriority());
        Logger.d(Constants.TAG, MSG + "CostTime: " + getCostTime());
        Logger.d(Constants.TAG, MSG + "-----------------------------------------------------");
    }

}