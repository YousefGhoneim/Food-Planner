package com.example.footplanner.ui.foryou.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.footplanner.R;
import com.example.footplanner.db.MealModel;
import com.example.footplanner.model.Meal;
import com.example.footplanner.ui.favourite.presenter.OnMealFavouriteListener;
import com.example.footplanner.ui.planned.presenter.OnMealPlannedListener;
import com.example.footplanner.ui.planned.presenter.PlannedView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private Context context;
    private List<MealModel> mealsList;
    private List<Meal> savedMeals = new ArrayList<>();
    private static final String TAG = "RecipeAdapter";

    private OnMealPlannedListener onMealPlannedListener; // Callback interface
    private OnMealFavouriteListener onMealFavouriteListener;

    public RecipeAdapter(Context context, List<MealModel> mealsList , OnMealPlannedListener listener , OnMealFavouriteListener favouriteListener) {
        this.context = context;
        this.mealsList = mealsList;
        this.onMealPlannedListener = listener;
        this.onMealFavouriteListener = favouriteListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealModel mealModel = mealsList.get(position);
        Meal meal = mealModel.getMeal();

        if (meal != null) {
            Glide.with(context)
                    .load(meal.getStrMealThumb())
                    .apply(new RequestOptions().override(200, 200)
                            .placeholder(R.drawable.nophotoavailable)
                            .error(R.drawable.ic_launcher_foreground))
                    .into(holder.img_card);

            holder.txt_card.setText(meal.getStrMeal());

            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("meal_id", meal.getIdMeal()); // Pass meal ID

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_homeFragment2_to_detailedFragment, bundle);
            });
        }

        holder.floatingActionButtonMealPlan.setOnClickListener(v -> {
            Meal plannedMeal = mealModel.getMeal();
            showDatePicker(plannedMeal, holder);
        });

        // Favorite Button Click
        holder.floatingActionButtonFav.setOnClickListener(v -> {
            if (mealModel != null) {
                toggleMealFavouriteStatus(mealModel, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_card;
        private TextView txt_card;
        private FloatingActionButton floatingActionButtonFav;
        private FloatingActionButton floatingActionButtonMealPlan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_card = itemView.findViewById(R.id.img_card);
            txt_card = itemView.findViewById(R.id.txt_card);
            floatingActionButtonFav = itemView.findViewById(R.id.fab_add_favorite);
            floatingActionButtonMealPlan = itemView.findViewById(R.id.fab_add_mealplan);
        }
    }
    private void showDatePicker(Meal meal, ViewHolder holder) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    Calendar maxDate = Calendar.getInstance();
                    maxDate.add(Calendar.DAY_OF_MONTH, 7);

                    if (selectedDate.before(Calendar.getInstance()) || selectedDate.after(maxDate)) {
                        holder.floatingActionButtonMealPlan.setImageResource(R.drawable.baseline_add_24);
                        Toast.makeText(context, "Please select a date within the next 7 days.", Toast.LENGTH_SHORT).show();
                    } else {
                        long dateMillis = selectedDate.getTimeInMillis();
                        holder.floatingActionButtonMealPlan.setImageResource(R.drawable.baseline_remove_24);

                        if (onMealPlannedListener != null) {
                            onMealPlannedListener.onMealPlanned(meal, dateMillis);
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set min and max date
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }
    private void toggleMealFavouriteStatus(MealModel mealModel, ViewHolder holder) {
        if (mealModel.isFavourite()) {
            mealModel.setFavourite(false);
            holder.floatingActionButtonFav.setImageResource(R.drawable.bookmarkadd);
        } else  {
            mealModel.setFavourite(true);
            holder.floatingActionButtonFav.setImageResource(R.drawable.baseline_favorite_24);
        }
        if (onMealFavouriteListener != null) {
            onMealFavouriteListener.onMealFavouriteClicked(mealModel.getMeal());
        }
    }

}
