package com.example.footplanner.ui.favourite.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.footplanner.db.MealModel;
import com.example.footplanner.model.Meal;
import com.example.footplanner.repo.MealRepo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavouritePresenter {
    private FavouriteView favouriteView;
    private MealRepo mealRepo;
    private String userId;
    private CompositeDisposable disposables = new CompositeDisposable();

    public FavouritePresenter(FavouriteView favouriteView, MealRepo mealRepo, Context context) {
        this.favouriteView = favouriteView;
        this.mealRepo = mealRepo;
        this.userId = getUserId(context);
    }

    public void toggleMealFavouriteStatus(Meal meal) {
        disposables.add(
                mealRepo.toggleMealFavouriteStatus(meal, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {

                                    favouriteView.showError("Meal favourite status updated!");
                                },
                                throwable -> favouriteView.showError("Failed to update favourite status: " + throwable.getMessage())
                        )
        );
    }
    public void getFavoriteMeals() {
        disposables.add(
                mealRepo.getFavoriteMeals(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(mealModels -> {
                            // Extract Meal objects from MealModel
                            List<Meal> meals = new ArrayList<>();
                            for (MealModel model : mealModels) {
                                meals.add(model.getMeal()); // Extract the Meal object
                            }
                            return meals;
                        })
                        .subscribe(
                                meals -> {
                                    if (meals.isEmpty()) {
                                        favouriteView.showEmptyState();
                                    } else {
                                        favouriteView.showFavoriteMeals(meals);
                                    }
                                },
                                throwable -> favouriteView.showError(throwable.getMessage())
                        )
        );
    }


    public void clear() {
        disposables.clear();
    }

    private String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getString("user_id", "guestUser"); // Default to guestUser
    }
}
