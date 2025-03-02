package com.example.footplanner.ui.filter.presenter;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.footplanner.repo.MealRepo;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FilteringPresenter {
    private FilteringView view;
    private MealRepo repo;

    public FilteringPresenter(FilteringView view, MealRepo repo) {
        this.view = view;
        this.repo = repo;
    }

    @SuppressLint("CheckResult")
    public void getMealsByIngredient(String ingredient) {
        repo.getMealsByIngredient(ingredient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            Log.d("FilteringPresenter", "Fetched " + meals.size() + " meals for ingredient: " + ingredient);
                            if (meals != null && !meals.isEmpty()) {
                                view.showMeals(meals);
                            } else {
                                view.showError("No meals found for ingredient: " + ingredient);
                            }
                        },
                        throwable -> view.showError("Error fetching meals: " + throwable.getMessage())
                );
    }

    @SuppressLint("CheckResult")
    public void getMealsByCategory(String category) {
        repo.getMealsByCategory(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            Log.d("FilteringPresenter", "Fetched " + meals.size() + " meals for category: " + category);
                            if (meals != null && !meals.isEmpty()) {
                                view.showMeals(meals);
                            } else {
                                view.showError("No meals found for category: " + category);
                            }
                        },
                        throwable -> view.showError("Error fetching meals: " + throwable.getMessage())
                );
    }

    @SuppressLint("CheckResult")
    public void getMealsByCountry(String country) {
        repo.getMealsByCountry(country)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            Log.d("FilteringPresenter", "Fetched " + meals.size() + " meals for country: " + country);
                            if (meals != null && !meals.isEmpty()) {
                                view.showMeals(meals);
                            } else {
                                view.showError("No meals found for country: " + country);
                            }
                        },
                        throwable -> view.showError("Error fetching meals: " + throwable.getMessage())
                );
    }
}
