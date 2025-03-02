package com.example.footplanner.network;

import android.content.Context;
import android.util.Log;

import com.example.footplanner.db.MealDao;
import com.example.footplanner.db.AppDataBase;
import com.example.footplanner.db.MealModel;
import com.example.footplanner.model.*;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductRemoteDataSource {
    private static ApiService apiService;
    private static ProductRemoteDataSource instance;
    private final MealDao mealDao;
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static final String TAG = "ProductRemoteDataSource";

    private ProductRemoteDataSource(Context context) {
        AppDataBase db = AppDataBase.getInstance(context);
        mealDao = db.mealDao();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ProductRemoteDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new ProductRemoteDataSource(context);
        }
        return instance;
    }

    public Single<MealResponse> getMealById(String mealId) {
        return apiService.getMealById(mealId)
                .subscribeOn(Schedulers.io());
    }

    public Completable saveMeal(MealModel meal) {
        return mealDao.insertMeal(meal)
                .subscribeOn(Schedulers.io())
                .andThen(Completable.fromAction(() -> mealDao.enforceCacheLimit(meal.getUserId())));
    }

    public Flowable<List<MealModel>> getFavoriteMeals(String userId) {
        return mealDao.getFavoriteMeals(userId)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<MealModel>> getPlannedMeals(String userId) {
        return mealDao.getPlannedMeals(userId)
                .subscribeOn(Schedulers.io());
    }

    public Completable deleteOldMeals(String userId, Long cutoffDate) {
        return mealDao.deleteOldMeals(cutoffDate, userId)
                .subscribeOn(Schedulers.io());
    }

    private MealModel mapApiMealToLocal(Meal apiMeal, MealModel localMeal) {
        return new MealModel(
                localMeal.getMealId(),
                localMeal.getUserId(),
                localMeal.getDate(),
                localMeal.isPlanned(),
                localMeal.isFavourite(),
                apiMeal
        );
    }

    public Single<MealModel> getRandomMeal(String userId) {
        return apiService.getRandomMeal()
                .map(mealResponse -> mealResponse.getMeals().get(0))
                .map(meal -> convertToMealModel(meal, userId))
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<MealModel>> getMealsByFirstLetter(String letter, String userId) {
        return apiService.getMealByFirstLetter(letter)
                .map(mealResponse -> {
                    List<MealModel> mealModels = new ArrayList<>();
                    for (Meal meal : mealResponse.getMeals()) {
                        mealModels.add(convertToMealModel(meal, userId));
                    }
                    return mealModels;
                })
                .toFlowable()
                .subscribeOn(Schedulers.io());
    }

    private MealModel convertToMealModel(Meal meal, String userId) {
        return new MealModel(
                meal.getIdMeal(),
                userId,
                System.currentTimeMillis(),
                false,
                false,
                meal
        );
    }
    public Single<CategoryResponse> getCategories() {
        return apiService.getMealCategories();
    }

    public Single<CountryResponse> getCountries() {
        return apiService.getMealCountries();
    }
    public Single<IngredientResponse> getIngredients() {
        return apiService.getMealIngredients();
    }
    public Single<List<MealSpecification>> getMealsByIngredient(String ingredient) {
        Log.d("API_CALL", "Fetching meals by ingredient: " + ingredient);
        return apiService.getMealsByIngredient(ingredient)
                .map(response -> (List<MealSpecification>) response.getMealsFromIngredient())
                .doOnSuccess(meals -> Log.d("API_RESPONSE", "Received " + meals.size() + " meals"))
                .doOnError(throwable -> Log.e("API_ERROR", "Error fetching meals", throwable));
    }

    public Single<List<MealSpecification>> getMealsByCategory(String category) {
        Log.d("API_CALL", "Fetching meals by category: " + category);
        return apiService.getMealsByCategory(category)
                .map(response -> (List<MealSpecification>) response.getMealsFromCategory())
                .doOnSuccess(meals -> Log.d("API_RESPONSE", "Received " + meals.size() + " meals"))
                .doOnError(throwable -> Log.e("API_ERROR", "Error fetching meals", throwable));
    }

    public Single<List<MealSpecification>> getMealsByCountry(String country) {
        Log.d("API_CALL", "Fetching meals by country: " + country);
        return apiService.getMealsByCountry(country)
                .map(response -> (List<MealSpecification>) response.getMealsFromCountry())
                .doOnSuccess(meals -> Log.d("API_RESPONSE", "Received " + meals.size() + " meals"))
                .doOnError(throwable -> Log.e("API_ERROR", "Error fetching meals", throwable));
    }


}
