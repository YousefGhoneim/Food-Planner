package com.example.footplanner.utilts;

import androidx.room.TypeConverter;

import com.example.footplanner.model.Meal;
import com.google.gson.Gson;

public class Converter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromMeal(Meal meal) {
        return gson.toJson(meal);
    }

    @TypeConverter
    public static Meal toMeal(String mealJson) {
        return gson.fromJson(mealJson, Meal.class);
    }
}
