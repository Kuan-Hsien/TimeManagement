package com.realizeitstudio.deteclife.object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.realizeitstudio.deteclife.utils.Constants;
import com.realizeitstudio.deteclife.utils.Logger;

/**
 * Created by Ken on 2018/10/27
 */
@Entity(tableName = "color_define_table")    // table-name (case-sensitive)
public class ColorDefineTable {

//    @PrimaryKey
//    @NonNull
//    @ColumnInfo(name = "color_id")
//    private String mColorId;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "color_name")
    private String mColorName;

    @ColumnInfo(name = "is_user_def")
    private Boolean isUserDef;

    @ColumnInfo(name = "update_date")
    private String mUpdateDate;


    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
    @NonNull
    public String getColorName() {
        return mColorName;
    }

    public Boolean getUserDef() {
        return isUserDef;
    }

    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setColorName(@NonNull String colorName) {
        mColorName = colorName;
    }

    public void setUserDef(Boolean userDef) {
        isUserDef = userDef;
    }

    public void setUpdateDate(String updateDate) {
        mUpdateDate = updateDate;
    }

    // Constructors
    public ColorDefineTable() {

    }

    public ColorDefineTable(@NonNull String colorName, Boolean isUserDef) {
        mColorName = colorName;
        this.isUserDef = isUserDef;
    }

    public ColorDefineTable(@NonNull String colorName, Boolean isUserDef, String updateDate) {
        mColorName = colorName;
        this.isUserDef = isUserDef;
        mUpdateDate = updateDate;
    }

    private static final String MSG = "ColorDefineTable: ";

    public void logD() {
        Logger.d(Constants.TAG, MSG + "--------------------- Color ----------------------");
        Logger.d(Constants.TAG, MSG + "ColorName: " + getColorName());
        Logger.d(Constants.TAG, MSG + "IsUserDef: " + getUserDef());
        Logger.d(Constants.TAG, MSG + "UpdateDate: " + getUpdateDate());
        Logger.d(Constants.TAG, MSG + "-----------------------------------------------------");
    }
}
