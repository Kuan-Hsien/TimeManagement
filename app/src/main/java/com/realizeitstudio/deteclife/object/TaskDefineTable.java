package com.realizeitstudio.deteclife.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;


/**
 * Created by Ken on 2018/9/24
 */
@Entity(tableName = "task_define_table",    // table-name (case-sensitive)
        primaryKeys = {"category_name", "task_name"})
public class TaskDefineTable {

    @NonNull
    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @NonNull
    @ColumnInfo(name = "task_name")
    private String mTaskName;

    @ColumnInfo(name = "task_color")
    private String mTaskColor;

    @ColumnInfo(name = "task_icon")
    private String mTaskIcon;

    @ColumnInfo(name = "task_priority")
    private int mTaskPriority;

    @ColumnInfo(name = "is_user_def")
    private Boolean isUserDef;

    @ColumnInfo(name = "update_date")
    private String mUpdateDate;


    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
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

    public int getTaskPriority() {
        return mTaskPriority;
    }

    public Boolean getUserDef() {
        return isUserDef;
    }

    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setCategoryName(String categoryName) {
        this.mCategoryName = categoryName;
    }

    public void setTaskName(String taskName) {
        this.mTaskName = taskName;
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

    public void setUserDef(Boolean userDef) {
        isUserDef = userDef;
    }

    public void setUpdateDate(String updateDate) {
        mUpdateDate = updateDate;
    }

    // Constructors
    public TaskDefineTable() {

    }

    public TaskDefineTable(@NonNull String categoryName, @NonNull String taskName, String taskColor, String taskIcon, int taskPriority, Boolean isUserDef) {
        mCategoryName = categoryName;
        mTaskName = taskName;
        mTaskColor = taskColor;
        mTaskIcon = taskIcon;
        mTaskPriority = taskPriority;
        this.isUserDef = isUserDef;
    }

    public TaskDefineTable(@NonNull String categoryName, @NonNull String taskName, String taskColor, String taskIcon, int taskPriority, Boolean isUserDef, String updateDate) {
        mCategoryName = categoryName;
        mTaskName = taskName;
        mTaskColor = taskColor;
        mTaskIcon = taskIcon;
        mTaskPriority = taskPriority;
        this.isUserDef = isUserDef;
        mUpdateDate = updateDate;
    }


    private static final String MSG = "TaskDefineTable: ";

    public void logD() {
        Logger.d(Constants.TAG, MSG + "--------------------- Task -------------------------");
        Logger.d(Constants.TAG, MSG + "TaskPriority: " + getTaskPriority());
        Logger.d(Constants.TAG, MSG + "CategoryName: " + getCategoryName());
        Logger.d(Constants.TAG, MSG + "TaskName: " + getTaskName());
        Logger.d(Constants.TAG, MSG + "TaskColor: " + getTaskColor());
        Logger.d(Constants.TAG, MSG + "---------------------------------------------------");
    }
}
