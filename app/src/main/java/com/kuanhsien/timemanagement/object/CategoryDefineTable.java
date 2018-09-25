package com.kuanhsien.timemanagement.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


/**
 * Created by Ken on 2018/9/24
 */
@Entity(tableName = "category_define_table")    // table-name (case-sensitive)
public class CategoryDefineTable {

    @PrimaryKey(autoGenerate = true)
    private int uid;                            // 加上 autoGenerate = true 代表想要讓 uid 自動增加

    @ColumnInfo(name = "category_name")
    private String categoryName;

    @ColumnInfo(name = "is_user_def")
    private Boolean isUserDef;

    @ColumnInfo(name = "category_color")
    private String categoryColor;

    @ColumnInfo(name = "category_icon")
    private String categoryIcon;

    @ColumnInfo(name = "category_priority")
    private String categoryPriority;


    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
    public int getUid() {
        return uid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Boolean getUserDef() {
        return isUserDef;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public String getCategoryPriority() {
        return categoryPriority;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setUserDef(Boolean userDef) {
        isUserDef = userDef;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public void setCategoryPriority(String categoryPriority) {
        this.categoryPriority = categoryPriority;
    }


    // Constructors
    public CategoryDefineTable() {

    }

    public CategoryDefineTable(int uid, String categoryName, Boolean isUserDef, String categoryColor, String categoryIcon, String categoryPriority) {
        this.uid = uid;
        this.categoryName = categoryName;
        this.isUserDef = isUserDef;
        this.categoryColor = categoryColor;
        this.categoryIcon = categoryIcon;
        this.categoryPriority = categoryPriority;
    }
}
