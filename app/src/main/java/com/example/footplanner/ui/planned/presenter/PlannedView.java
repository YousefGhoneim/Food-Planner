package com.example.footplanner.ui.planned.presenter;

import com.example.footplanner.db.MealModel;

import java.util.List;

public interface PlannedView {
    void showPlannedMeals(List<MealModel> meals);
    void onMealAdded();
    void onMealDeleted();
    void showError(String message);
    void onMealPlanned(MealModel meal, long date);
}
