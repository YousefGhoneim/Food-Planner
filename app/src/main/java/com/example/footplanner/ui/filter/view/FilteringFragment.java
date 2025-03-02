package com.example.footplanner.ui.filter.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.footplanner.R;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.model.Meal;
import com.example.footplanner.model.MealSpecification;
import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;
import com.example.footplanner.ui.filter.presenter.FilteringPresenter;
import com.example.footplanner.ui.filter.presenter.FilteringView;
import com.example.footplanner.ui.filter.presenter.OnfilteringClickListener;

import java.util.ArrayList;
import java.util.List;


public class FilteringFragment extends Fragment implements FilteringView, OnfilteringClickListener {

    private FilteringPresenter presenter;
    private RecyclerView mealRecyclerView;
    private SearchView mealSearchView;
    private FilteringAdapter filterAdapter;
    private List<MealSpecification> allMeals = new ArrayList<>();
    private List<MealSpecification> filteredMeals = new ArrayList<>();

    private String filterType;
    private String filterValue;

    public FilteringFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filtered, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealRecyclerView = view.findViewById(R.id.recyclerViewSearchResult);
        mealSearchView = view.findViewById(R.id.search_view);
        LottieAnimationView waitingAnimation = view.findViewById(R.id.waitingAnimation);
        waitingAnimation.setVisibility(View.VISIBLE);
        mealRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if (getArguments() != null) {
            filterType = getArguments().getString("type");
            filterValue = getArguments().getString("id");
        }

        presenter = new FilteringPresenter(this, MealRepo.getInstance(ProductLocalDataSource.getInstance(requireContext()), ProductRemoteDataSource.getInstance(requireContext())));

        filterAdapter = new FilteringAdapter(getContext(), new ArrayList<>(), meal -> {
            Toast.makeText(getContext(), "Clicked: " + meal.getStrMeal(), Toast.LENGTH_SHORT).show();
        });
        mealRecyclerView.setAdapter(filterAdapter);
        waitingAnimation.setVisibility(View.GONE);
        mealRecyclerView.setVisibility(View.VISIBLE);
        fetchMeals();

        setupSearchView();
    }

    private void fetchMeals() {
        if (filterType.equals("ingredient")) {
            presenter.getMealsByIngredient(filterValue);
        } else if (filterType.equals("category")) {
            presenter.getMealsByCategory(filterValue);
        } else if (filterType.equals("country")) {
            presenter.getMealsByCountry(filterValue);
        }
    }

    private void setupSearchView() {
        mealSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMeals(newText);
                return true;
            }
        });
    }

    private void filterMeals(String query) {
        filteredMeals.clear();
        query = query.toLowerCase();
        for (MealSpecification meal : allMeals) {
            if (meal.getStrMeal().toLowerCase().contains(query)) {
                filteredMeals.add(meal);
            }
        }
        filterAdapter.updateData(filteredMeals);
    }

    @Override
    public void showMeals(List<MealSpecification> meals) {
        allMeals.clear();
        allMeals.addAll(meals);
        filterAdapter.updateData(allMeals);
    }


    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealClick(MealSpecification meal) {

    }
}