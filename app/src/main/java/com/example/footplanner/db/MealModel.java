package com.example.footplanner.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.example.footplanner.utilts.Converter;
import com.example.footplanner.utilts.DateConverter;
import com.example.footplanner.model.Meal;

import java.util.Date;

import androidx.annotation.NonNull;

@Entity(tableName = "meal" , primaryKeys = {"mealId" , "userId"})
@TypeConverters({Converter.class , DateConverter.class})
public class MealModel {
    @ColumnInfo (name = "mealId")
    @NonNull
    private String mealId;

    @ColumnInfo (name = "userId")
    @NonNull
    private String userId;

    @ColumnInfo (name = "date")
    private long date;

    @ColumnInfo (name = "isPlanned")
    private boolean isPlanned;

    @ColumnInfo (name = "isFavourite")
    private boolean isFavourite;

    @ColumnInfo (name = "meal")
    private Meal meal;

    public MealModel() {
        this.isFavourite = false;
        this.isPlanned = false;
        this.date = System.currentTimeMillis();
    }

    public MealModel(@NonNull String mealId, @NonNull String userId, long date, boolean isPlanned, boolean isFavourite, Meal meal) {
        this.mealId = mealId;
        this.userId = userId;
        this.date = (date > 0) ? date : System.currentTimeMillis();
        this.isPlanned = isPlanned;
        this.isFavourite = isFavourite;
        this.meal = meal;
    }

    public @NonNull String getMealId() {
        return mealId;
    }

    public void setMealId(@NonNull String mealId) {
        this.mealId = mealId;
    }

    public @NonNull String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isPlanned() {
        return isPlanned;
    }

    public void setPlanned(boolean planned) {
        isPlanned = planned;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }
}
