package com.example.footplanner.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.footplanner.model.Meal;
import com.example.footplanner.db.MealModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
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

    public Completable updatePlannedMeal(String userId, long date, String mealId, MealModel meal) {
        return mealDao.updatePlannedMeal(userId, date, mealId, meal.getMeal())
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
        return Completable.fromAction(() -> {
                    // Check if the meal already exists in the database
                    MealModel existingMeal = mealDao.getMealById(meal.getIdMeal(), userId).blockingGet();
                    if (existingMeal != null) {
                        // Update the existing meal's planned status and date
                        existingMeal.setPlanned(true);
                        existingMeal.setDate(dateMillis);
                        mealDao.updatePlannedMeal(userId, dateMillis, meal.getIdMeal(), existingMeal.getMeal()).blockingAwait();
                    } else {
                        MealModel mealModel = new MealModel(
                                meal.getIdMeal(),
                                userId,
                                dateMillis,
                                true,  // isPlanned
                                false, // isFavourite
                                meal   // Meal object
                        );
                        mealDao.insertMeal(mealModel).blockingAwait();
                    }
                }).subscribeOn(Schedulers.io())
                .doOnError(error -> Log.e(TAG, "Error planning meal", error));
    }

    public Completable addMealToFavorites(Meal meal, String userId, boolean isFavourite) {
        MealModel mealModel = new MealModel(
                meal.getIdMeal(),
                userId,
                System.currentTimeMillis(),
                false,
                isFavourite,
                meal
        );
        return mealDao.insertMeal(mealModel)
                .subscribeOn(Schedulers.io());
    }

    public Completable toggleMealFavouriteStatus(Meal meal, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference favouritesRef = db.collection("users").document(userId).collection("favourites");

        return Completable.create(emitter -> {
            Log.d(TAG, "Checking if meal exists in Firestore: " + meal.getIdMeal());
            favouritesRef.document(meal.getIdMeal()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Meal exists in Firestore, removing it: " + meal.getIdMeal());
                            favouritesRef.document(meal.getIdMeal()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Meal removed from Firestore, updating Room DB: " + meal.getIdMeal());
                                        mealDao.updateMealFavouriteStatus(meal.getIdMeal(), userId, false)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(
                                                        () -> {
                                                            Log.d(TAG, "Meal removed from Room DB successfully!");
                                                            emitter.onComplete();
                                                        },
                                                        error -> {
                                                            Log.e(TAG, "Error updating meal in Room", error);
                                                            emitter.onError(error);
                                                        }
                                                );
                                    })
                                    .addOnFailureListener(error -> {
                                        Log.e(TAG, "Error removing meal from Firestore", error);
                                        emitter.onError(error);
                                    });
                        } else {
                            Log.d(TAG, "Meal does not exist in Firestore, adding it: " + meal.getIdMeal());
                            Map<String, Object> mealData = new HashMap<>();
                            mealData.put("mealId", meal.getIdMeal());

                            favouritesRef.document(meal.getIdMeal()).set(mealData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Meal added to Firestore, inserting into Room DB: " + meal.getIdMeal());
                                        MealModel mealModel = new MealModel(
                                                meal.getIdMeal(),
                                                userId,
                                                System.currentTimeMillis(),
                                                false,
                                                true,
                                                meal
                                        );

                                        mealDao.insertMeal(mealModel)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(
                                                        () -> {
                                                            Log.d(TAG, "Meal inserted into Room successfully!");
                                                            emitter.onComplete();
                                                        },
                                                        error -> {
                                                            Log.e(TAG, "Error inserting meal into Room", error);
                                                            emitter.onError(error);
                                                        }
                                                );
                                    })
                                    .addOnFailureListener(error -> {
                                        Log.e(TAG, "Error adding meal to Firestore", error);
                                        emitter.onError(error);
                                    });
                        }
                    })
                    .addOnFailureListener(error -> {
                        Log.e(TAG, "Error checking meal in Firestore", error);
                        emitter.onError(error);
                    });
        }).subscribeOn(Schedulers.io());
    }

    public Completable clearUserMeals(String userId) {
        return mealDao.deleteAllMealsForUser(userId)
                .subscribeOn(Schedulers.io());
    }
}