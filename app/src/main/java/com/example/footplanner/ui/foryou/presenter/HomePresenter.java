package com.example.footplanner.ui.foryou.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.footplanner.db.MealModel;
import com.example.footplanner.repo.MealRepo;
import java.util.Calendar;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter {
    private final MealRepo mealRepo;
    private final HomeView homeView;
    private final String userId;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public HomePresenter(HomeView homeView, MealRepo mealRepo, Context context) {
        this.homeView = homeView;
        this.mealRepo = mealRepo;
        this.userId = getUserId(context);
    }

    public void getMealByDate(long date) {
        disposables.add(mealRepo.getMealByDate(userId, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meal -> homeView.showRandomMeal(meal),
                        error -> getNewRandomMealForToday(date)
                ));
    }

    private void getNewRandomMealForToday(long date) {
        disposables.add(mealRepo.getRandomMealForToday(userId, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        homeView::showRandomMeal,
                        error -> homeView.showError(error.getMessage())
                ));
    }

    public void getRecommendedMeals() {
        disposables.add(mealRepo.getRecommendedMeals(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(homeView::showRecommendedMeals, error -> homeView.showError(error.getMessage())));
    }

    public void clear() {
        disposables.clear();
    }

    private String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getString("user_id", "guestUser"); // Default to guestUser
    }

    public long getCurrentDateTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
