package com.example.footplanner.ui.favourite.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.footplanner.R;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.model.Meal;
import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;
import com.example.footplanner.ui.favourite.presenter.FavouritePresenter;
import com.example.footplanner.ui.favourite.presenter.FavouriteView;
import com.example.footplanner.ui.favourite.presenter.OnMealFavouriteListener;
import java.util.ArrayList;
import java.util.List;

public class FavouritFragment extends Fragment implements FavouriteView, OnMealFavouriteListener {
    private FavouritePresenter presenter;
    private FavouriteAdapter adapter;
    private RecyclerView recyclerView;
    private List<Meal> meals = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.favRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize Adapter
        adapter = new FavouriteAdapter(requireContext(), meals, this);
        recyclerView.setAdapter(adapter);

        // Initialize Presenter
        presenter = new FavouritePresenter(
                this,
                MealRepo.getInstance(
                        ProductLocalDataSource.getInstance(requireContext()),
                        ProductRemoteDataSource.getInstance(requireContext())
                ),
                requireContext()
        );

        // Fetch favorite meals
        presenter.getFavoriteMeals();
    }

    @Override
    public void showFavoriteMeals(List<Meal> meals) {
        this.meals.clear();
        this.meals.addAll(meals);
        adapter.notifyDataSetChanged(); // Update RecyclerView
    }

    @Override
    public void showEmptyState() {
        this.meals.clear();
        adapter.notifyDataSetChanged(); // Show empty state
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealFavouriteClicked(Meal meal) {
        presenter.toggleMealFavouriteStatus(meal);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.clear();
    }
}
