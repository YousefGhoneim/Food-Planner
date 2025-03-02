package com.example.footplanner.ui.favourite.presenter;

import com.example.footplanner.db.MealModel;
import com.example.footplanner.model.Meal;

import java.util.List;

public interface FavouriteView {
    void showFavoriteMeals(List<Meal> meal);
    void showEmptyState();
    void showError(String message);
}
