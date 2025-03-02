package com.example.footplanner.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.footplanner.repo.MealRepo;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter implements HomeContract {
    private HomeContract view;
    private MealRepo mealRepo;
    private String userId;
    private Context context;
    private CompositeDisposable disposables = new CompositeDisposable();

    public HomePresenter(HomeContract view, MealRepo mealRepo, Context context) {
        this.view = view;
        this.mealRepo = mealRepo;
        this.context = context;
        this.userId = getUserId(context);
    }

    @Override
    public void switchFragment(int position) {
        // Handle fragment switching logic
    }

    @Override
    public void onNavigationItemSelected(int newIndex) {
        // Handle navigation item selection logic
    }

    @Override
    public void showError(String message) {
        view.showError(message);
    }

    @Override
    public void onUserMealsCleared() {
        view.onUserMealsCleared();
    }

    @Override
    public void onLogoutSuccess() {
        view.onLogoutSuccess();
    }

    @Override
    public void onLocalDatabaseCleared() {
        view.onLocalDatabaseCleared();
    }

    public void clearUserMeals(String userId) {
        disposables.add(
                mealRepo.clearUserMeals(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> view.onUserMealsCleared(),
                                throwable -> view.showError(throwable.getMessage())
                        )
        );
    }

    public void logout() {
        // Clear user session data from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Clear the local database for the current user
        clearLocalDatabase();
    }

    private void clearLocalDatabase() {
        disposables.add(
                mealRepo.clearUserMeals(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    Log.d("HomePresenter", "Local database cleared for user: " + userId);
                                    view.onLocalDatabaseCleared();
                                },
                                throwable -> {
                                    Log.e("HomePresenter", "Error clearing local database", throwable);
                                    view.showError(throwable.getMessage());
                                }
                        )
        );
    }

    private String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getString("user_id", "guestUser"); // Default to guestUser
    }

    public void clear() {
        disposables.clear();
    }
}