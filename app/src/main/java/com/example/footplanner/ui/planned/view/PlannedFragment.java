package com.example.footplanner.ui.planned.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.footplanner.R;
import com.example.footplanner.db.MealModel;
import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.ui.planned.presenter.PlannedPresenter;
import com.example.footplanner.ui.planned.presenter.PlannedView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannedFragment extends Fragment implements PlannedView {

    private PlannedPresenter presenter;
    private Map<String, PlannedAdapter> adaptersByDay;
    private Map<String, List<MealModel>> mealsByDay;

    private RecyclerView recyclerViewPlannedSaturday;
    private RecyclerView recyclerViewPlannedSunday;
    private RecyclerView recyclerViewPlannedMonday;
    private RecyclerView recyclerViewPlannedTuesday;
    private RecyclerView recyclerViewPlannedWednesday;
    private RecyclerView recyclerViewPlannedThursday;
    private RecyclerView recyclerViewPlannedFriday;

    public PlannedFragment() {
        // Required empty public constructor
    }

    public static PlannedFragment newInstance() {
        return new PlannedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProductLocalDataSource localDataSource = ProductLocalDataSource.getInstance(getContext());
        presenter = new PlannedPresenter(MealRepo.getInstance(ProductLocalDataSource.getInstance(getContext()), ProductRemoteDataSource.getInstance(getContext())),this  , requireActivity());

        mealsByDay = new HashMap<>();
        adaptersByDay = new HashMap<>();

        String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (String day : days) {
            mealsByDay.put(day, new ArrayList<>());
            adaptersByDay.put(day, new PlannedAdapter(getContext(), mealsByDay.get(day), presenter));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planned, container, false);

        recyclerViewPlannedSaturday = setupRecyclerView(view, R.id.recyclerViewSaturday, "Saturday");
        recyclerViewPlannedSunday = setupRecyclerView(view, R.id.recyclerViewSunday, "Sunday");
        recyclerViewPlannedMonday = setupRecyclerView(view, R.id.recyclerViewMonday, "Monday");
        recyclerViewPlannedTuesday = setupRecyclerView(view, R.id.recyclerViewTuesday, "Tuesday");
        recyclerViewPlannedWednesday = setupRecyclerView(view, R.id.recyclerViewWednesday, "Wednesday");
        recyclerViewPlannedThursday = setupRecyclerView(view, R.id.recyclerViewThursday, "Thursday");
        recyclerViewPlannedFriday = setupRecyclerView(view, R.id.recyclerViewFriday, "Friday");

        presenter.loadPlannedMeals();
        return view;
    }

    private RecyclerView setupRecyclerView(View view, int recyclerViewId, String day) {
        RecyclerView recyclerView = view.findViewById(recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adaptersByDay.get(day));
        return recyclerView;
    }

    @Override
    public void showPlannedMeals(List<MealModel> meals) {
        // Clear previous data
        for (String day : mealsByDay.keySet()) {
            mealsByDay.get(day).clear();
        }

        // Distribute meals by day
        for (MealModel meal : meals) {
            String dayName = getDayFromTimestamp(meal.getDate());
            if (mealsByDay.containsKey(dayName)) {
                mealsByDay.get(dayName).add(meal);
            }
        }

        // Notify each adapter
        for (String day : adaptersByDay.keySet()) {
            adaptersByDay.get(day).notifyDataSetChanged();
        }
    }

    private String getDayFromTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE");
        return sdf.format(new java.util.Date(timestamp));
    }

    @Override
    public void onMealAdded() {
        presenter.loadPlannedMeals();
    }

    @Override
    public void onMealDeleted() {
        presenter.loadPlannedMeals();
    }

    @Override
    public void showError(String message) {
        // Handle errors, e.g., show a Toast
    }

    @Override
    public void onMealPlanned(MealModel meal, long date) {
        meal.setDate(date);
        presenter.addPlannedMeal(meal);
    }
}
