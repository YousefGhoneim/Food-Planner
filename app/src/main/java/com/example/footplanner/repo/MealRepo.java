package com.example.footplanner.repo;

import com.example.footplanner.db.MealModel;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.network.ProductRemoteDataSource;

import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealRepo {
    private final ProductLocalDataSource localDataSource;
    private final ProductRemoteDataSource remoteDataSource;
    private static MealRepo repo;

    public MealRepo(ProductLocalDataSource localDataSource, ProductRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public static MealRepo getInstance(ProductLocalDataSource localDataSource, ProductRemoteDataSource remoteDataSource) {
        if (repo == null) {
            repo = new MealRepo(localDataSource, remoteDataSource);
        }
        return repo;
    }

    //  Get Meal by ID (First check local, then remote)
    public Single<MealModel> getMealById(String userId, String mealId) {
        return localDataSource.getMealById(userId, mealId)
                .onErrorResumeNext(error -> remoteDataSource.getMealById(userId, mealId)
                        .flatMap(meal -> saveMeal(meal).andThen(Single.just(meal))))
                .subscribeOn(Schedulers.io());
    }

    public Completable saveMeal(MealModel meal) {
        return localDataSource.insertMeal(meal)
                .andThen(localDataSource.enforceCacheLimit(meal.getUserId()))
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<MealModel>> getFavoriteMeals(String userId) {
        return localDataSource.getFavoriteMeals(userId)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<MealModel>> getPlannedMeals(String userId) {
        return localDataSource.getPlannedMeals(userId)
                .subscribeOn(Schedulers.io());
    }

    public Single<MealModel> getMealByDate(String userId, long date) {
        return localDataSource.getMealByDate(userId, date)
                .subscribeOn(Schedulers.io());
    }

    public Single<MealModel> getRandomMealForToday(String userId, long date) {
        return getMealByDate(userId, date)
                .onErrorResumeNext(error -> remoteDataSource.getRandomMeal(userId)
                        .flatMap(meal -> {
                            meal.setDate(date);
                            meal.setPlanned(true);
                            return saveMeal(meal).andThen(Single.just(meal));
                        }))
                .subscribeOn(Schedulers.io());
    }

    public Completable updatePlannedMeal(String userId, long date, String mealId, MealModel meal) {
        return localDataSource.updatePlannedMeal(userId, date, mealId, meal.getMeal())
                .subscribeOn(Schedulers.io());
    }

    public Completable deleteMeal(String userId, String mealId) {
        return localDataSource.deleteMeal(userId, mealId)
                .subscribeOn(Schedulers.io());
    }

    public Completable deleteOldMeals(String userId, Long cutoffDate) {
        return localDataSource.deleteOldMeals(userId, cutoffDate)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<MealModel>> getRecommendedMeals(String userId) {
        String randomLetter = getRandomLetter();
        return remoteDataSource.getMealsByFirstLetter(randomLetter, userId)
                .subscribeOn(Schedulers.io());
    }

    private String getRandomLetter() {
        Random random = new Random();
        char randomChar = (char) ('a' + random.nextInt(26));
        return String.valueOf(randomChar);
    }
}
