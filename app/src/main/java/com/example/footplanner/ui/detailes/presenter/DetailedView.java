package com.example.footplanner.ui.detailes.presenter;

import com.example.footplanner.model.Meal;
import com.example.footplanner.model.MealSpecification;

public interface DetailedView {
    void showMealDetails(Meal meal);
    void showError(String message);
}
