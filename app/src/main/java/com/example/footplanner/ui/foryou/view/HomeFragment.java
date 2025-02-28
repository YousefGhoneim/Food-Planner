package com.example.footplanner.ui.foryou.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footplanner.R;
import com.example.footplanner.db.MealModel;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.model.Meal;
import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;
import com.example.footplanner.ui.foryou.presenter.HomePresenter;
import com.example.footplanner.ui.foryou.presenter.HomeView;
import com.example.footplanner.ui.planned.presenter.OnMealPlannedListener;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements HomeView , OnMealPlannedListener {

    private HomePresenter homePresenter;
    private RecipeAdapter recipeAdapter;
    private RecipeAdapter dailyMealAdapter;
    private CarouselRecyclerview recommendedMealsRecyclerView;
    private CarouselRecyclerview dailyRecyclerView;
    private List<MealModel> recommendedMealsList = new ArrayList<>();
    private List<MealModel> dailyMealList = new ArrayList<>();

    private Toolbar customToolbar;
    private ImageView menuIcon, appLogo;
    private TextView profileIcon;

    private static final String PREFS_NAME = "daily_meal_prefs";
    private static final String DAILY_MEAL_KEY = "daily_meal_id";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        customToolbar = view.findViewById(R.id.custom_toolbar);
        menuIcon = view.findViewById(R.id.menuIcon);
        appLogo = view.findViewById(R.id.appLogo);
        profileIcon = view.findViewById(R.id.profileIcon);

        // Daily RecyclerView setup
        dailyRecyclerView = view.findViewById(R.id.dailyRecyclerView);
        dailyRecyclerView.setHasFixedSize(true);
        dailyRecyclerView.setAlpha(true);
        dailyRecyclerView.setInfinite(true);
        dailyMealAdapter = new RecipeAdapter(getContext(), dailyMealList,this);
        dailyRecyclerView.setAdapter(dailyMealAdapter);

        // Recommended Meals RecyclerView setup
        recommendedMealsRecyclerView = view.findViewById(R.id.recommenddeddailyRecyclerView);
        recommendedMealsRecyclerView.setHasFixedSize(true);
        recommendedMealsRecyclerView.setAlpha(true);
        recommendedMealsRecyclerView.setInfinite(true);
        recipeAdapter = new RecipeAdapter(getContext(), recommendedMealsList,this);
        recommendedMealsRecyclerView.setAdapter(recipeAdapter);

        homePresenter = new HomePresenter(
                this,
                MealRepo.getInstance(ProductLocalDataSource.getInstance(requireContext()), ProductRemoteDataSource.getInstance(requireContext())),
                requireContext()
        );

        loadDailyMeal();
        homePresenter.getRecommendedMeals();

        return view;
    }

    private void loadDailyMeal() {
        long today = homePresenter.getCurrentDateTimestamp();

        homePresenter.getMealByDate(today);
    }

    @Override
    public void showRandomMeal(MealModel meal) {
        if (meal != null && meal.getMeal() != null) {
            dailyMealList.clear();
            dailyMealList.add(meal);
            dailyMealAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "No daily inspiration meal available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showRecommendedMeals(List<MealModel> meals) {
        recommendedMealsList.clear();
        recommendedMealsList.addAll(meals);
        recipeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homePresenter.clear();
    }

    @Override
    public void onMealPlanned(Meal meal, long dateMillis) {
        // Use the HomePresenter to save the meal to the database
//        homePresenter.addPlannedMeal(mealModel);

        // Show a success message to the user
        Toast.makeText(getContext(), "Meal planned for " + new Date(dateMillis).toString(), Toast.LENGTH_SHORT).show();
    }
}
