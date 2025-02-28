package com.example.footplanner.ui.planned.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.footplanner.db.MealModel;
import com.example.footplanner.repo.MealRepo;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlannedPresenter {
    private final MealRepo mealRepo;
    private final PlannedView view;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final String userId;

    public PlannedPresenter(MealRepo mealRepo, PlannedView view, Context context) {
        this.mealRepo = mealRepo;
        this.view = view;
        this.userId = getUserId(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void addPlannedMeal(MealModel meal) {
        meal.setPlanned(true);
        disposables.add(
                mealRepo.getMealByDate(userId, meal.getDate()) // Get meals for the selected date
                        .subscribeOn(Schedulers.io())
                        .flatMapCompletable(existingMeals -> {
                            if (existingMeals.isEmpty()) {
                                return mealRepo.saveMeal(meal);
                            } else {
                                return Completable.merge(existingMeals.stream()
                                        .map(existingMeal -> {
                                            if (existingMeal.getMealId().equals(meal.getMealId())) {
                                                existingMeal.setMeal(meal.getMeal());
                                                return mealRepo.updatePlannedMeal(userId, meal.getDate(), existingMeal.getMealId(), existingMeal);
                                            }
                                            return Completable.complete();
                                        })
                                        .toList()
                                ).andThen(mealRepo.saveMeal(meal)); // Save if the meal doesn't already exist
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> view.onMealAdded(), throwable -> view.showError(throwable.getMessage()))
        );
    }






    public void loadPlannedMeals() {
        disposables.add(
                mealRepo.getPlannedMeals(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::showPlannedMeals, throwable -> {
                            Log.e("DEBUG", "Error loading planned meals", throwable);
                            view.showPlannedMeals(new ArrayList<>());
                        })
        );
    }



    public void deletePlannedMeal(String mealId, long date) {
        Log.d("DEBUG", "deletePlannedMeal called with mealId: " + mealId + " and date: " + date);

        disposables.add(
                mealRepo.deleteMeal(userId, mealId, date)
                        .doOnComplete(() -> Log.d("DEBUG", "Meal deleted successfully: " + mealId))
                        .doOnError(error -> Log.e("DEBUG", "Error deleting meal", error))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> view.onMealDeleted(), throwable -> view.showError(throwable.getMessage()))
        );
    }


    public void dispose() {
        disposables.dispose();
    }
    private String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getString("user_id", "guestUser"); // Default to guestUser
    }
}
