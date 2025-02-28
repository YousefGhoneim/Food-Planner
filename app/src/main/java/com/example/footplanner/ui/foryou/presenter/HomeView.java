package com.example.footplanner.ui.foryou.presenter;

import com.example.footplanner.db.MealModel;

import java.util.List;

public interface HomeView {
    void showRandomMeal(MealModel meal);
    void showRecommendedMeals(List<MealModel> meals);
    void showError(String error);
}
