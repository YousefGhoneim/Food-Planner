package com.example.footplanner.repo;

import android.util.Log;

import com.example.footplanner.db.MealModel;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.model.CategoryResponse;
import com.example.footplanner.model.CountryResponse;
import com.example.footplanner.model.IngredientResponse;
import com.example.footplanner.model.Meal;
import com.example.footplanner.model.MealResponse;
import com.example.footplanner.model.MealSpecification;
import com.example.footplanner.network.ProductRemoteDataSource;

import java.util.ArrayList;
import java.util.Collections;
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

    public Single<MealResponse> getMealById(String mealId) {
        return remoteDataSource.getMealById(mealId)
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

    public Single<List<MealModel>> getMealByDate(String userId, long date) {
        return localDataSource.getMealsByDate(userId, date)
                .subscribeOn(Schedulers.io())
                .flatMap(mealModels -> {
                    List<MealModel> plannedMeals = new ArrayList<>();
                    for (MealModel meal : mealModels) {
                        if (meal.isPlanned()) {
                            plannedMeals.add(meal);
                        }
                    }
                    return Single.just(plannedMeals);
                });
    }

    public Single<List<MealModel>> getRandomMealForToday(String userId, long date) {
        return getMealByDate(userId, date) // Get meals from local DB
                .doOnSuccess(meals -> Log.d("DEBUG", "Local meals found: " + meals.size()))
                .onErrorResumeNext(error -> {
                    Log.e("DEBUG", "Error fetching meals from local DB, trying remote", error);
                    return Single.just(Collections.emptyList());
                })
                .flatMap(meals -> {
                    if (meals.isEmpty()) {
                        Log.d("DEBUG", "No local meals found, fetching from remote...");
                        return remoteDataSource.getRandomMeal(userId)
                                .doOnSuccess(meal -> Log.d("DEBUG", "Fetched remote meal: " + meal.getMealId()))
                                .flatMap(meal -> {
                                    meal.setDate(date);
                                    meal.setPlanned(true);
                                    return saveMeal(meal)
                                            .doOnComplete(() -> Log.d("DEBUG", "Remote meal saved to DB"))
                                            .doOnError(error -> Log.e("DEBUG", "Error saving meal", error))
                                            .andThen(Single.just(Collections.singletonList(meal)));
                                });
                    } else {
                        Log.d("DEBUG", "Returning local meals...");
                        return Single.just(meals);
                    }
                })
                .subscribeOn(Schedulers.io());
    }




    public Completable updatePlannedMeal(String userId, long date, String mealId, MealModel meal) {
        return localDataSource.updatePlannedMeal(userId, date, mealId, meal)
                .subscribeOn(Schedulers.io());
    }

    public Completable deleteMeal(String userId, String mealId, long date) {
        Log.d("DEBUG", "deleteMeal in Repo called for mealId: " + mealId + " on date: " + date);

        return localDataSource.deleteMeal(userId, mealId, date)
                .doOnComplete(() -> Log.d("DEBUG", "Meal deleted from DB: " + mealId))
                .doOnError(error -> Log.e("DEBUG", "Error in MealRepo.deleteMeal", error));
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

    public Completable planMeal(String userId, Meal meal, long dateMillis) {
        return localDataSource.planMeal(userId, meal, dateMillis);
    }

    public Completable toggleMealFavouriteStatus(Meal meal, String userId) {
        return localDataSource.toggleMealFavouriteStatus(meal, userId)
                .subscribeOn(Schedulers.io());
    }

    public Single<CategoryResponse> getCategories() {
        return remoteDataSource.getCategories();
    }

    public Single<CountryResponse> getCountries() {
        return remoteDataSource.getCountries();
    }
    public  Single<IngredientResponse> getIngredients() {
        return remoteDataSource.getIngredients();
    }
    public Single<List<MealSpecification>> getMealsByIngredient(String ingredient) {
        return remoteDataSource.getMealsByIngredient(ingredient);
    }
    public Single<List<MealSpecification>> getMealsByCategory(String category) {
        return remoteDataSource.getMealsByCategory(category);
    }
    public Single<List<MealSpecification>> getMealsByCountry(String country) {
        return remoteDataSource.getMealsByCountry(country);
    }
    public Completable clearUserMeals(String userId) {
        return localDataSource.clearUserMeals(userId)
                .subscribeOn(Schedulers.io());
    }

}
