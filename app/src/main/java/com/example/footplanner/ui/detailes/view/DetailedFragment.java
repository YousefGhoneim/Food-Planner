package com.example.footplanner.ui.detailes.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.footplanner.R;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.model.Ingredient;
import com.example.footplanner.model.Meal;
import com.example.footplanner.model.MealSpecification;
import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;
import com.example.footplanner.ui.detailes.presenter.DetailedPresenter;
import com.example.footplanner.ui.detailes.presenter.DetailedView;
import com.example.footplanner.ui.detailes.presenter.OnDetailedClickListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class DetailedFragment extends Fragment implements DetailedView, OnDetailedClickListener {
    private ImageView imageMeal;
    private TextView textRecipeTitle, textCountry, textRecipeDescription, textStepsDescription;
    private RecyclerView recyclerViewIngredients;
    private WebView webViewDetails;
    private Button btnAddToPlan;
    private DetailedPresenter presenter;
    private DetailedAdapter ingredientsAdapter;
    private String mealId;

    public DetailedFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detailed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageMeal = view.findViewById(R.id.imageMeal);
        textRecipeTitle = view.findViewById(R.id.textRecipeTitle);
        textCountry = view.findViewById(R.id.textCountry);
        textRecipeDescription = view.findViewById(R.id.textRecipeDescription);
        textStepsDescription = view.findViewById(R.id.textStepsDescription);
        recyclerViewIngredients = view.findViewById(R.id.recyclerViewIngredients);
        webViewDetails = view.findViewById(R.id.webViewDetails);
        btnAddToPlan = view.findViewById(R.id.btnAddToPlan);

        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        ingredientsAdapter = new DetailedAdapter(getContext(), new ArrayList<>() , new ArrayList<>());
        recyclerViewIngredients.setAdapter(ingredientsAdapter);

        // Get the meal ID from the bundle
        if (getArguments() != null) {
            mealId = getArguments().getString("meal_id");
        }

        presenter = new DetailedPresenter(this , MealRepo.getInstance(ProductLocalDataSource.getInstance(requireContext()), ProductRemoteDataSource.getInstance(requireContext())));
        presenter.getMealDetails(mealId);
    }

    @Override
    public void showMealDetails(Meal meal) {
        Glide.with(this).load(meal.getStrMealThumb()).into(imageMeal);
        textRecipeTitle.setText(meal.getStrMeal());
        textCountry.setText(meal.getStrArea());
        textRecipeDescription.setText(meal.getStrInstructions());

        loadYouTubeVideo(meal.getStrYoutube());

        // Get ingredient and measurement lists separately
        List<String> ingredients = meal.getNonNullIngredients();
        List<String> measurements = meal.getNonNullMeasurements();

        // Update RecyclerView Adapter with separate lists
        ingredientsAdapter.updateData(ingredients, measurements);
    }


    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void loadYouTubeVideo(String youtubeUrl) {
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            String videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("=") + 1);
            String embedUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=1&rel=0";

            webViewDetails.getSettings().setJavaScriptEnabled(true);
            webViewDetails.getSettings().setDomStorageEnabled(true);
            webViewDetails.setWebViewClient(new WebViewClient());
            webViewDetails.loadUrl(embedUrl);
        } else {
            webViewDetails.loadUrl("about:blank"); // If no video, clear WebView
        }
    }


}