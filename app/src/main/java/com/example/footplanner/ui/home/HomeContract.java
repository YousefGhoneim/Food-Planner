package com.example.footplanner.ui.home;

public interface HomeContract {
        void switchFragment(int position);
        void onNavigationItemSelected(int newIndex);
        void showError(String message);
        void onUserMealsCleared();
        void onLogoutSuccess();
        void onLocalDatabaseCleared();
}