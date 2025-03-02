package com.example.footplanner.ui.foryou.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.footplanner.db.MealModel;
import com.example.footplanner.model.Meal;
import com.example.footplanner.repo.MealRepo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter {
    private final MealRepo mealRepo;
    private final HomeView homeView;
    private final String userId;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public HomePresenter(HomeView homeView, MealRepo mealRepo, Context context) {
        this.homeView = homeView;
        this.mealRepo = mealRepo;
        this.userId = getUserId(context);
    }

    public void getMealByDate(long date) {
        if (mealRepo == null) {
            Log.e("DEBUG", "mealRepo is NULL!");
            return;
        }
        if (homeView == null) {
            Log.e("DEBUG", "homeView is NULL!");
            return;
        }
        disposables.add(
                mealRepo.getMealByDate(userId, date)
                        .subscribeOn(Schedulers.io())
                        .flatMap(meals -> {
                            Log.d("DEBUG", "Meals from DB: " + meals.size());
                            if (meals.isEmpty()) {
                                return mealRepo.getRandomMealForToday(userId, date);
                            } else {
                                return Single.just(meals);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                homeView::showRandomMeal,
                                error -> {
                                    Log.e("DEBUG", "Error fetching meal", error);
                                    homeView.showError(error.getMessage());
                                }
                        )
        );
    }


    private void getNewRandomMealForToday(long date) {
        disposables.add(mealRepo.getRandomMealForToday(userId, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        homeView::showRandomMeal,
                        error -> homeView.showError(error.getMessage())
                ));
    }

    public void getRecommendedMeals() {
        disposables.add(mealRepo.getRecommendedMeals(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(homeView::showRecommendedMeals, error -> homeView.showError(error.getMessage())));
    }

    public void clear() {
        disposables.clear();
    }

    private String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getString("user_id", "guestUser"); // Default to guestUser
    }

    public long getCurrentDateTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @SuppressLint("CheckResult")
    public void planMeal(Meal meal, long dateMillis) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference plannedMealRef = db.collection("users").document(userId)
                .collection("planned").document(meal.getIdMeal());

        disposables.add(
                Completable.create(emitter -> {
                            plannedMealRef.get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        MealModel mealModel = new MealModel(
                                                meal.getIdMeal(),
                                                userId,
                                                dateMillis,
                                                true, // isPlanned
                                                false, // isFavourite
                                                meal
                                        );

                                        if (documentSnapshot.exists()) {
                                            // Meal already planned, update dates
                                            List<Long> dates = (List<Long>) documentSnapshot.get("dates");
                                            if (dates == null) {
                                                dates = new ArrayList<>();
                                            }

                                            if (!dates.contains(dateMillis)) {
                                                dates.add(dateMillis);

                                                plannedMealRef.update("dates", dates)
                                                        .addOnSuccessListener(aVoid -> {
                                                            // Insert into Room Database
                                                            mealRepo.planMeal(userId, meal, dateMillis)
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(
                                                                            () -> emitter.onComplete(),
                                                                            error -> {
                                                                                Log.e("planMeal", "Error inserting planned meal into Room", error);
                                                                                emitter.onError(error);
                                                                            }
                                                                    );
                                                        })
                                                        .addOnFailureListener(emitter::onError);
                                            } else {
                                                emitter.onComplete(); // Already planned
                                            }
                                        } else {
                                            // First time planning this meal
                                            Map<String, Object> mealData = new HashMap<>();
                                            mealData.put("mealId", meal.getIdMeal());
                                            mealData.put("dates", Collections.singletonList(dateMillis));

                                            plannedMealRef.set(mealData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Insert into Room Database
                                                        mealRepo.planMeal(userId, meal, dateMillis)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(
                                                                        () -> emitter.onComplete(),
                                                                        error -> {
                                                                            Log.e("planMeal", "Error inserting planned meal into Room", error);
                                                                            emitter.onError(error);
                                                                        }
                                                                );
                                                    })
                                                    .addOnFailureListener(emitter::onError);
                                        }
                                    })
                                    .addOnFailureListener(emitter::onError);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> homeView.onMealAdded(),
                                throwable -> homeView.showError(throwable.getMessage())
                        )
        );
    }




    public void deletePlannedMeal(String mealId, long date) {
        disposables.add(
                mealRepo.deleteMeal(userId, mealId, date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> homeView.onMealDeleted(), throwable -> homeView.showError(throwable.getMessage()))
        );
    }
    public void toggleMealFavouriteStatus(Meal meal) {
        disposables.add(
                mealRepo.toggleMealFavouriteStatus(meal , userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    Log.i("TAG", "toggleMealFavouriteStatus: meal added ");
                                    homeView.showError("Meal favourite status updated!");
                                },
                                throwable -> homeView.showError("Failed to update favourite status: " + throwable.getMessage())
                        )
        );
    }
}