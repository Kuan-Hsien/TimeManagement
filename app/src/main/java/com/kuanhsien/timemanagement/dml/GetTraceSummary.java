package com.kuanhsien.timemanagement.dml;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;

import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;


/**
 * Created by Ken on 2018/10/08
 */
public class GetTraceSummary {

    private static final String MSG = "GetTraceSummary: ";

    @NonNull
    @ColumnInfo(name = "ver_no")
    private String mVerNo;

    @NonNull
    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @NonNull
    @ColumnInfo(name = "task_name")
    private String mTaskName;

    @ColumnInfo(name = "cost_time")
    private Long mCostTime;

    @ColumnInfo(name = "update_date")
    private Long mUpdateDate;


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

    public Long getCostTime() {
        return mCostTime;
    }

    public Long getUpdateDate() {
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

    public void setCostTime(Long costTime) {
        mCostTime = costTime;
    }

    public void setUpdateDate(Long updateDate) {
        mUpdateDate = updateDate;
    }

    public GetTraceSummary() {

    }

    public GetTraceSummary(GetTraceSummary bean) {
        mVerNo = bean.getVerNo();
        mCategoryName = bean.getCategoryName();
        mTaskName = bean.getTaskName();
        mCostTime = bean.getCostTime();
        mUpdateDate = bean.getUpdateDate();
    }

    public GetTraceSummary(@NonNull String verNo, @NonNull String categoryName, @NonNull String taskName, Long costTime) {
        mVerNo = verNo;
        mCategoryName = categoryName;
        mTaskName = taskName;
        mCostTime = costTime;
    }

    public GetTraceSummary(@NonNull String verNo, @NonNull String categoryName, @NonNull String taskName, Long costTime, Long updateDate) {
        mVerNo = verNo;
        mCategoryName = categoryName;
        mTaskName = taskName;
        mCostTime = costTime;
        mUpdateDate = updateDate;
    }

    public void LogD () {
        Logger.d(Constants.TAG, MSG + "--------------------- GetTraceSummary -------------------------");
        Logger.d(Constants.TAG, MSG + "VerNo: " + getVerNo());
        Logger.d(Constants.TAG, MSG + "CategoryName: " + getCategoryName());
        Logger.d(Constants.TAG, MSG + "TaskName: " + getTaskName());
        Logger.d(Constants.TAG, MSG + "CostTime: " + getCostTime());
        Logger.d(Constants.TAG, MSG + "-----------------------------------------------------");
    }
}
