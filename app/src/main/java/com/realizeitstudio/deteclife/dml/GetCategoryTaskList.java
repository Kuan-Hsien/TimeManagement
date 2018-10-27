package com.realizeitstudio.deteclife.dml;

import android.arch.persistence.room.ColumnInfo;

import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

/**
 * Created by Ken on 2018/9/30
 *
 * Query Result
 */
public class GetCategoryTaskList {

    @ColumnInfo(name = "item_catg")
    private String mItemCatg;

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

    // Getter

    public String getItemCatg() {
        return mItemCatg;
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

    // Setter

    public void setItemCatg(String itemCatg) {
        mItemCatg = itemCatg;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
    }

    public void setCategoryColor(String categoryColor) {
        mCategoryColor = categoryColor;
    }

    public void setCategoryPriority(int categoryPriority) {
        this.mCategoryPriority = categoryPriority;
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


    public GetCategoryTaskList(String itemCatg, String categoryName, String categoryColor, int categoryPriority, String taskName, String taskColor, String taskIcon, int taskPriority) {
        mItemCatg = itemCatg;
        mCategoryName = categoryName;
        mCategoryColor = categoryColor;
        mCategoryPriority = categoryPriority;
        mTaskName = taskName;
        mTaskColor = taskColor;
        mTaskIcon = taskIcon;
        mTaskPriority = taskPriority;
    }

    private static final String MSG = "GetCategoryTaskList: ";

    public void logD() {
        Logger.d(Constants.TAG, MSG + "----------------- Category-Tasks --------------------");
        Logger.d(Constants.TAG, MSG + "ItemCatg: " + getItemCatg());
        Logger.d(Constants.TAG, MSG + "CategoryName: " + getCategoryName());
        Logger.d(Constants.TAG, MSG + "CategoryColor: " + getCategoryColor());
        Logger.d(Constants.TAG, MSG + "CategoryPriority: " + getCategoryPriority());
        Logger.d(Constants.TAG, MSG + "TaskName: " + getTaskName());
        Logger.d(Constants.TAG, MSG + "TaskColor: " + getTaskColor());
        Logger.d(Constants.TAG, MSG + "TaskPriority: " + getTaskPriority());
        Logger.d(Constants.TAG, MSG + "-----------------------------------------------------");
    }

}
