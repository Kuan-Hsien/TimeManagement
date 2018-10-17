package com.realizeitstudio.deteclife.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.realizeitstudio.deteclife.object.CategoryDefineTable;
import com.realizeitstudio.deteclife.object.TaskDefineTable;
import com.realizeitstudio.deteclife.object.TimePlanningTable;
import com.realizeitstudio.deteclife.object.TimeTracingTable;

/**
 * Created by Ken on 2018/9/24
 */
@Database(entities = {TimePlanningTable.class, TimeTracingTable.class, CategoryDefineTable.class, TaskDefineTable.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    public static AppDatabase getDatabase(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    "time_magnagement.db").build();
        }
        return sInstance;
    }

    public static void onDestroy() {
        sInstance = null;
    }

    public abstract DatabaseDao getDatabaseDao();



}