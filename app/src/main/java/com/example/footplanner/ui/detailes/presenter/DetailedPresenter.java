package com.example.footplanner.ui.detailes.presenter;

import android.annotation.SuppressLint;

import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DetailedPresenter {
    private DetailedView view;
    private MealRepo repo;

    public DetailedPresenter(DetailedView view ,  MealRepo repo) {
        this.view = view;
        this.repo = repo;
    }

    @SuppressLint("CheckResult")
    public void getMealDetails(String mealId) {
        repo.getMealById(mealId)
                .map(mealResponse -> {
                    if (mealResponse.getMeals() != null && !mealResponse.getMeals().isEmpty()) {
                        return mealResponse.getMeals().get(0);
                    } else {
                        throw new RuntimeException("Meal not found!");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meal -> view.showMealDetails(meal),
                        throwable -> view.showError("Error fetching meal details: " + throwable.getMessage())
                );
    }



}
