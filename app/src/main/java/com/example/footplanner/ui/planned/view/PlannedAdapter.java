package com.example.footplanner.ui.planned.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.footplanner.R;
import com.example.footplanner.db.MealModel;
import com.example.footplanner.ui.planned.presenter.PlannedPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PlannedAdapter extends RecyclerView.Adapter<PlannedAdapter.ViewHolder>{
    private final Context context;
    private final List<MealModel> mealList;
    private final PlannedPresenter presenter;

    public PlannedAdapter(Context context, List<MealModel> mealList, PlannedPresenter presenter) {
        this.context = context;
        this.mealList = mealList;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_mealplan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealModel meal = mealList.get(position);

        holder.txtMealName.setText(meal.getMeal().getStrMeal());
        Glide.with(context).load(meal.getMeal().getStrMealThumb()).into(holder.imgMeal);

        holder.removeButton.setOnClickListener(v -> {
            presenter.deletePlannedMeal(meal.getMealId());
            mealList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMealName;
        ImageView imgMeal;
        FloatingActionButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMealName = itemView.findViewById(R.id.txt_card_mealplan);
            imgMeal = itemView.findViewById(R.id.img_card_mealplan);
            removeButton = itemView.findViewById(R.id.mealplan_remove_favorite);
        }
    }
}
