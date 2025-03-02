package com.example.footplanner.ui.detailes.view;

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
import com.example.footplanner.model.Ingredient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class DetailedAdapter extends RecyclerView.Adapter<DetailedAdapter.ViewHolder> {
    private Context context;
    private List<String> ingredients;
    private List<String> measurements;

    public DetailedAdapter(Context context, List<String> ingredients , List<String> measurements) {
        this.context = context;
        this.ingredients = ingredients;
        this.measurements = measurements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        String measurement = measurements.get(position);
        holder.txtIngredient.setText(ingredient);
        holder.txtMeasurement.setText(measurement);
        String imageUrl = "https://www.themealdb.com/images/ingredients/" + ingredient + "-Small.png";
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imgIngredient);
    }




    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void updateData(List<String> newIngredients, List<String> newMeasurements) {
        ingredients.clear();
        ingredients.addAll(newIngredients);
        measurements.clear();
        measurements.addAll(newMeasurements);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIngredient;
        ImageView imgIngredient;
        TextView txtMeasurement;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIngredient = itemView.findViewById(R.id.txtIngredient);
            imgIngredient = itemView.findViewById(R.id.imgIngredient);
            txtMeasurement = itemView.findViewById(R.id.txtMeasurement);
        }
    }

}
