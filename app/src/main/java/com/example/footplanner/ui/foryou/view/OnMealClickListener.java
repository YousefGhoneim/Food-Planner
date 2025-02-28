package com.example.footplanner.ui.foryou.view;

import com.example.footplanner.model.Meal;

public interface OnMealClickListener {
    void onAddToFavoriteClick(Meal meal);
    void onRemoveFromFavoriteClick(Meal meal);
}
