package com.yumin.pomodoro.data.repository.room;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Mission.class}, version = 1, exportSchema = false)
public abstract class MissionDBManager extends RoomDatabase {
    private final static String TAG = "[MissionDBManager]";
    private final static String DB_NAME = "mysql.db";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static MissionDBManager instance;

    public static synchronized MissionDBManager getInstance(Context context) {
        // singleton
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    static MissionDBManager create (Context context){
        return Room.databaseBuilder(
                context,
                MissionDBManager.class,
                DB_NAME).build();
    }

    public abstract MissionDao getMissionDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}