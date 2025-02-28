package com.example.footplanner.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.footplanner.utilts.Converter;
import com.example.footplanner.utilts.DateConverter;

@Database(entities = {MealModel.class}, version = 6 , exportSchema = true)
@TypeConverters({DateConverter.class, Converter.class})
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase instance;
    public abstract MealDao mealDao();
    public static synchronized AppDataBase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "meal_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE meal ADD COLUMN datee INTEGER NOT NULL DEFAULT 0");
        }
    };
}
