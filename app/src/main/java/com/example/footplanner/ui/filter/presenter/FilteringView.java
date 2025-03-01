package com.example.footplanner.ui.filter.presenter;

import com.example.footplanner.model.Meal;
import com.example.footplanner.model.MealSpecification;

import java.util.List;

public interface FilteringView {
    void showMeals(List<MealSpecification> meals);
    void showError(String message);
}
