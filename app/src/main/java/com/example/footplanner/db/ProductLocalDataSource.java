package com.example.footplanner.db;

import android.content.Context;

import com.example.footplanner.model.Meal;

import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductLocalDataSource {
    private static ProductLocalDataSource instance;
    private final MealDao mealDao;
    private static final String TAG = "ProductLocalDataSource";

    private ProductLocalDataSource(Context context) {
        mealDao = AppDataBase.getInstance(context).mealDao();
    }

    public static synchronized ProductLocalDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new ProductLocalDataSource(context);
        }
        return instance;
    }

    public Completable insertMeal(MealModel meal) {
        return mealDao.insertMeal(meal)
                .subscribeOn(Schedulers.io()); // Run on background thread
    }

    public Single<MealModel> getMealById(String userId, String mealId) {
        return mealDao.getMealById(mealId, userId)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<MealModel>> getAllMeals(String userId) {
        return mealDao.getAllMeals(userId)
                .subscribeOn(Schedulers.io());
    }

    public Single<MealModel> getMealByDate(String userId, long date) {
        return mealDao.getMealByDate(userId, date)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<MealModel>> getFavoriteMeals(String userId) {
        return mealDao.getFavoriteMeals(userId)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<MealModel>> getPlannedMeals(String userId) {
        return mealDao.getPlannedMeals(userId)
                .subscribeOn(Schedulers.io());
    }

    public Completable updatePlannedMeal(String userId, long date, String mealId, Meal meal) {
        return mealDao.updatePlannedMeal(userId, date, mealId, meal)
                .subscribeOn(Schedulers.io());
    }

    // 🔹 Delete a Meal
    public Completable deleteMeal(String userId, String mealId) {
        return mealDao.deleteMeal(mealId, userId)
                .subscribeOn(Schedulers.io());
    }

    // 🔹 Delete Old Meals (Meals in the past)
    public Completable deleteOldMeals(String userId, Long cutoffDate) {
        return mealDao.deleteOldMeals(cutoffDate, userId)
                .subscribeOn(Schedulers.io());
    }

    public Completable enforceCacheLimit(String userId) {
        return mealDao.enforceCacheLimit(userId)
                .subscribeOn(Schedulers.io());
    }
}
