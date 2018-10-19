package com.realizeitstudio.deteclife.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;


/**
 * Created by Ken on 2018/10/18
 */
@Entity(tableName = "icon_define_table")    // table-name (case-sensitive)
public class IconDefineTable {

//    @PrimaryKey
//    @NonNull
//    @ColumnInfo(name = "icon_id")
//    private String mIconId;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "icon_name")
    private String mIconName;

    @ColumnInfo(name = "is_user_def")
    private Boolean isUserDef;

    @ColumnInfo(name = "update_date")
    private String mUpdateDate;


    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
    @NonNull
    public String getIconName() {
        return mIconName;
    }

    public Boolean getUserDef() {
        return isUserDef;
    }

    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setIconName(@NonNull String iconName) {
        mIconName = iconName;
    }

    public void setUserDef(Boolean userDef) {
        isUserDef = userDef;
    }

    public void setUpdateDate(String updateDate) {
        mUpdateDate = updateDate;
    }

    // Constructors
    public IconDefineTable() {

    }

    public IconDefineTable(@NonNull String iconName, Boolean isUserDef) {
        mIconName = iconName;
        this.isUserDef = isUserDef;
    }

    public IconDefineTable(@NonNull String iconName, Boolean isUserDef, String updateDate) {
        mIconName = iconName;
        this.isUserDef = isUserDef;
        mUpdateDate = updateDate;
    }

    private static final String MSG = "IconDefineTable: ";

    public void LogD () {
        Logger.d(Constants.TAG, MSG + "--------------------- Icon ----------------------");
        Logger.d(Constants.TAG, MSG + "IconName: " + getIconName());
        Logger.d(Constants.TAG, MSG + "IsUserDef: " + getUserDef());
        Logger.d(Constants.TAG, MSG + "UpdateDate: " + getUpdateDate());
        Logger.d(Constants.TAG, MSG + "-----------------------------------------------------");
    }
}
