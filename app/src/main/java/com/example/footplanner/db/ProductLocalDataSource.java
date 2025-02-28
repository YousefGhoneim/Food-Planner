package com.example.footplanner.db;

import android.content.Context;
import android.util.Log;

import com.example.footplanner.model.Meal;

import java.util.Calendar;
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
                .doOnComplete(() -> Log.d("DEBUG", "Meal inserted successfully: " + meal.getMealId()))
                .doOnError(error -> Log.e("DEBUG", "Error inserting meal", error))
                .subscribeOn(Schedulers.io());
    }


    public Single<MealModel> getMealById(String userId, String mealId) {
        return mealDao.getMealById(mealId, userId)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<MealModel>> getAllMeals(String userId) {
        return mealDao.getAllMeals(userId)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<MealModel>> getMealsByDate(String userId, long date) {
        long startOfDay = getStartOfDay(date);
        long endOfDay = getEndOfDay(date);
        return mealDao.getMealsByDate(userId, startOfDay, endOfDay)
                .subscribeOn(Schedulers.io());
    }

    private long getStartOfDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
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


    public Completable deleteMeal(String userId, String mealId, long date) {
        long startOfDay = getStartOfDay(date);
        long endOfDay = getEndOfDay(date);
        return mealDao.deleteMeal(userId, mealId, startOfDay, endOfDay)
                .subscribeOn(Schedulers.io());
    }




    public Completable deleteOldMeals(String userId, Long cutoffDate) {
        return mealDao.deleteOldMeals(cutoffDate, userId)
                .subscribeOn(Schedulers.io());
    }

    public Completable enforceCacheLimit(String userId) {
        return mealDao.enforceCacheLimit(userId)
                .subscribeOn(Schedulers.io());
    }

    public Completable planMeal(String userId, Meal meal, long dateMillis) {
        MealModel mealModel = new MealModel(
                meal.getIdMeal(),
                userId,
                dateMillis,
                true,
                false,
                meal
        );
        return mealDao.insertMeal(mealModel);
    }

    public Completable addMealToFavorites(Meal meal , String userId , boolean isFavourite) {
        MealModel mealModel = new MealModel(
                meal.getIdMeal(),
                userId,
                System.currentTimeMillis(),
                false,
                isFavourite,
                meal
        );
        return mealDao.insertMeal(mealModel);
    }
    public Completable toggleMealFavouriteStatus(Meal meal, String userId) {
        return mealDao.getMealById(meal.getIdMeal(), userId)
                .flatMapCompletable(existingMeal -> {
                    // Toggle the isFavourite flag
                    boolean newFavouriteStatus = !existingMeal.isFavourite();
                    return mealDao.updateMealFavouriteStatus(meal.getIdMeal(), userId, newFavouriteStatus);
                })
                .onErrorResumeNext(error -> {
                    // If the meal doesn't exist, insert it with isFavourite = true
                    MealModel newMeal = new MealModel(
                            meal.getIdMeal(),
                            userId,
                            System.currentTimeMillis(), // Use current time as the date
                            false, // isPlanned
                            true, // isFavourite
                            meal
                    );
                    return mealDao.insertMeal(newMeal);
                })
                .subscribeOn(Schedulers.io());
    }

}
