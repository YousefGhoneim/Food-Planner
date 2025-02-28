package com.example.footplanner.ui.planned.presenter;

import com.example.footplanner.model.Meal;

public interface OnMealPlannedListener {
    void onMealPlanned(Meal meal, long dateMillis);

}
