package com.example.footplanner.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.footplanner.model.Meal;

import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeal(MealModel meal);

    @Query("DELETE FROM meal WHERE userId = :userId AND mealId = :mealId AND date BETWEEN :startOfDay AND :endOfDay")
    Completable deleteMeal(String userId, String mealId, long startOfDay, long endOfDay);


    @Query("SELECT * FROM meal WHERE userId = :userId")
    Single<List<MealModel>> getAllMeals(String userId);

    @Query("SELECT * FROM meal WHERE mealId = :mealId AND userId = :userId")
    Single<MealModel> getMealById(String mealId, String userId);

    @Query("SELECT * FROM meal WHERE userId = :userId AND date BETWEEN :startOfDay AND :endOfDay")
    Single<List<MealModel>> getMealsByDate(String userId, long startOfDay, long endOfDay);



    @Query("SELECT * FROM meal WHERE isPlanned = 1 AND userId = :userId ORDER BY date ASC")
    Flowable<List<MealModel>> getPlannedMeals(String userId);

    @Query("SELECT * FROM meal WHERE isFavourite = 1 AND userId = :userId")
    Flowable<List<MealModel>> getFavoriteMeals(String userId);

    @Query("DELETE FROM meal WHERE date < :cutoffDate AND isFavourite = 0 AND isPlanned = 0 AND userId = :userId")
    Completable deleteOldMeals(long cutoffDate, String userId);

    @Query("UPDATE meal SET mealId = :mealId, meal = :meal WHERE userId = :userId AND date = :date")
    Completable updatePlannedMeal(String userId, long date, String mealId, Meal meal);

    @Query("DELETE FROM meal WHERE mealId NOT IN ( " +
            "SELECT mealId FROM ( " +
            "SELECT mealId, date FROM meal WHERE userId = :userId AND (isFavourite = 1 OR isPlanned = 1) " +
            "UNION ALL " +
            "SELECT mealId, date FROM meal WHERE userId = :userId " +
            ") ORDER BY date DESC LIMIT 10)")
    Completable enforceCacheLimit(String userId);

    @Query("UPDATE meal SET isFavourite = :isFavourite WHERE mealId = :mealId AND userId = :userId")
    Completable updateMealFavouriteStatus(String mealId, String userId, boolean isFavourite);

        @Query("DELETE FROM meal WHERE userId = :userId")
        Completable deleteAllMealsForUser(String userId);

}
