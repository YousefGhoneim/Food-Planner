package com.example.footplanner.ui.filter.view;

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
import com.example.footplanner.model.Meal;
import com.example.footplanner.model.MealSpecification;
import com.example.footplanner.ui.filter.presenter.OnfilteringClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FilteringAdapter extends RecyclerView.Adapter<FilteringAdapter.ViewHolder>{
    private Context context;
    private List<MealSpecification> mealList;
    private OnfilteringClickListener listener;

    public FilteringAdapter(Context context, List<MealSpecification> mealList, OnfilteringClickListener listener) {
        this.context = context;
        this.mealList = mealList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public FilteringAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilteringAdapter.ViewHolder holder, int position) {
        MealSpecification meal = mealList.get(position);
        holder.mealName.setText(meal.getStrMeal());
        Glide.with(context).load(meal.getStrMealThumb()).into(holder.mealImage);

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public void updateData(List<MealSpecification> newMeals) {
        mealList.clear();
        mealList.addAll(newMeals);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mealName;
        ImageView mealImage;
        FloatingActionButton fabAddFavorite;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.txt_card);
            mealImage = itemView.findViewById(R.id.img_card);
            fabAddFavorite = itemView.findViewById(R.id.fab_add_favorite);
        }
    }
}
