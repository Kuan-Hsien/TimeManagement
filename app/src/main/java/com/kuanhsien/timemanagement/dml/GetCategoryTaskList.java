package com.kuanhsien.timemanagement.dml;

import android.arch.persistence.room.ColumnInfo;

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

}
