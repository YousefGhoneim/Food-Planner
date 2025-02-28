package com.example.footplanner.ui.planned.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.footplanner.db.MealModel;
import com.example.footplanner.repo.MealRepo;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

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

    public void addPlannedMeal(MealModel meal) {
        meal.setPlanned(true);
        disposables.add(
                mealRepo.getMealByDate(userId, meal.getDate())
                        .flatMapCompletable(existingMeal -> {
                            // If a meal already exists for this date, update it
                            existingMeal.setMeal(meal.getMeal());
                            return mealRepo.updatePlannedMeal(userId, meal.getDate(), existingMeal.getMealId(), existingMeal);
                        })
                        .onErrorResumeNext(error -> {
                            // If no meal exists for this date, save the new meal
                            return mealRepo.saveMeal(meal);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> view.onMealAdded(), throwable -> view.showError(throwable.getMessage()))
        );
    }

    public void loadPlannedMeals() {
        disposables.add(
                mealRepo.getPlannedMeals(userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::showPlannedMeals, throwable -> view.showError(throwable.getMessage()))
        );
    }

    public void deletePlannedMeal(String mealId) {
        disposables.add(
                mealRepo.deleteMeal(userId, mealId)
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
