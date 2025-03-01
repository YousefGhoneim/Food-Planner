package com.example.footplanner.ui.onboard.presenter;

import android.content.Context;
import android.content.SharedPreferences;

public class OnboardingRepository {
    private static final String PREF_NAME = "OnboardingPrefs";
    private static final String KEY_FIRST_TIME = "IsFirstTimeLaunch";
    private SharedPreferences sharedPreferences;

    public OnboardingRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME, isFirstTime).apply();
    }
}
