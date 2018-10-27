package com.realizeitstudio.deteclife.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;


/**
 * Created by Ken on 2018/9/24
 */
@Entity(tableName = "category_define_table")    // table-name (case-sensitive)
public class CategoryDefineTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @ColumnInfo(name = "category_color")
    private String mCategoryColor;

    @ColumnInfo(name = "category_priority")
    private int mCategoryPriority;

    @ColumnInfo(name = "is_user_def")
    private Boolean isUserDef;

    @ColumnInfo(name = "update_date")
    private String mUpdateDate;


    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.

    public String getCategoryName() {
        return mCategoryName;
    }

    public Boolean getUserDef() {
        return isUserDef;
    }

    public String getCategoryColor() {
        return mCategoryColor;
    }

    public int getCategoryPriority() {
        return mCategoryPriority;
    }

    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setCategoryName(String categoryName) {
        this.mCategoryName = categoryName;
    }

    public void setUserDef(Boolean userDef) {
        isUserDef = userDef;
    }

    public void setCategoryColor(String categoryColor) {
        this.mCategoryColor = categoryColor;
    }

    public void setCategoryPriority(int categoryPriority) {
        this.mCategoryPriority = categoryPriority;
    }

    public void setUpdateDate(String updateDate) {
        mUpdateDate = updateDate;
    }

    // Constructors
    public CategoryDefineTable() {

    }

    public CategoryDefineTable(@NonNull String categoryName, String categoryColor, int categoryPriority, Boolean isUserDef) {
        mCategoryName = categoryName;
        mCategoryColor = categoryColor;
        mCategoryPriority = categoryPriority;
        this.isUserDef = isUserDef;
    }

    public CategoryDefineTable(@NonNull String categoryName, String categoryColor, int categoryPriority, Boolean isUserDef, String updateDate) {
        mCategoryName = categoryName;
        mCategoryColor = categoryColor;
        mCategoryPriority = categoryPriority;
        this.isUserDef = isUserDef;
        mUpdateDate = updateDate;
    }

    private static final String MSG = "CategoryDefineTable: ";

    public void logD() {
        Logger.d(Constants.TAG, MSG + "--------------------- Category ----------------------");
        Logger.d(Constants.TAG, MSG + "CategoryPriority: " + getCategoryPriority());
        Logger.d(Constants.TAG, MSG + "CategoryName: " + getCategoryName());
        Logger.d(Constants.TAG, MSG + "CategoryColor: " + getCategoryColor());
        Logger.d(Constants.TAG, MSG + "-----------------------------------------------------");
    }
}

