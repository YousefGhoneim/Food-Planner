package com.example.footplanner.ui.search.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.footplanner.R;
import com.example.footplanner.model.Category;
import com.example.footplanner.model.Country;
import com.example.footplanner.model.Ingredient;
import com.example.footplanner.ui.search.presenter.OnSearchItemClickIistener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private static final String TAG = "SearchAdapter";
    private Context context;
    private List<Object> searchResults;
    private OnSearchItemClickIistener listener;

    private static final Map<String, String> COUNTRY_CODE_MAP = new HashMap<>();

    static {
        COUNTRY_CODE_MAP.put("American", "us");
        COUNTRY_CODE_MAP.put("British", "gb");
        COUNTRY_CODE_MAP.put("Canadian", "ca");
        COUNTRY_CODE_MAP.put("Chinese", "cn");
        COUNTRY_CODE_MAP.put("Croatian", "hr");
        COUNTRY_CODE_MAP.put("Dutch", "nl");
        COUNTRY_CODE_MAP.put("Egyptian", "eg");
        COUNTRY_CODE_MAP.put("French", "fr");
        COUNTRY_CODE_MAP.put("Greek", "gr");
        COUNTRY_CODE_MAP.put("Indian", "in");
        COUNTRY_CODE_MAP.put("Irish", "ie");
        COUNTRY_CODE_MAP.put("Italian", "it");
        COUNTRY_CODE_MAP.put("Jamaican", "jm");
        COUNTRY_CODE_MAP.put("Japanese", "jp");
        COUNTRY_CODE_MAP.put("Kenyan", "ke");
        COUNTRY_CODE_MAP.put("Malaysian", "my");
        COUNTRY_CODE_MAP.put("Mexican", "mx");
        COUNTRY_CODE_MAP.put("Moroccan", "ma");
        COUNTRY_CODE_MAP.put("Polish", "pl");
        COUNTRY_CODE_MAP.put("Portuguese", "pt");
        COUNTRY_CODE_MAP.put("Russian", "ru");
        COUNTRY_CODE_MAP.put("Spanish", "es");
        COUNTRY_CODE_MAP.put("Thai", "th");
        COUNTRY_CODE_MAP.put("Tunisian", "tn");
        COUNTRY_CODE_MAP.put("Turkish", "tr");
        COUNTRY_CODE_MAP.put("Vietnamese", "vn");
    }

    public SearchAdapter(Context context, List<Object> searchResults, OnSearchItemClickIistener listener) {
        this.context = context;
        this.searchResults = searchResults;
        this.listener = listener;
        Log.d(TAG, "Adapter initialized with " + searchResults.size() + " items.");
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        Object item = searchResults.get(position);
        Log.d(TAG, "Binding item at position: " + position + " | Type: " + item.getClass().getSimpleName());

        if (item instanceof Ingredient) {
            Ingredient ingredient = (Ingredient) item;
            holder.itemName.setText(ingredient.getStrIngredient());
            String imageUrl = "https://www.themealdb.com/images/ingredients/" + ingredient.getStrIngredient() + "-Small.png";
            holder.loadImage(imageUrl);
            Log.d(TAG, "Loaded Ingredient: " + ingredient.getStrIngredient());

        } else if (item instanceof Category) {
            Category category = (Category) item;
            holder.itemName.setText(category.getCategoryName());
            holder.loadImage(category.getCategoryImage());
            Log.d(TAG, "Loaded Category: " + category.getCategoryName());

        } else if (item instanceof Country) {
            Country country = (Country) item;
            holder.itemName.setText(country.getCountryName());
            String countryCode = COUNTRY_CODE_MAP.getOrDefault(country.getCountryName(), "xx");
            String flagUrl = "https://www.themealdb.com/images/icons/flags/big/64/" + countryCode + ".png";
            holder.loadImage(flagUrl);
            Log.d(TAG, "Loaded Country: " + country.getCountryName() + " | Flag URL: " + flagUrl);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Item clicked at position: " + position);
            if (listener != null) {
                listener.onSearchItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + searchResults.size());
        return searchResults.size();
    }

    public void updateData(List<Object> newResults) {
        if (newResults != null) {
            Log.d(TAG, "Updating data: New size = " + newResults.size());

            searchResults.clear();
            searchResults.addAll(newResults);
            notifyDataSetChanged();

            Log.d(TAG, "Adapter data size after update: " + searchResults.size());
        } else {
            Log.e(TAG, "updateData called with null list!");
        }
    }


    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private ImageView itemImage;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.ingredientName);
            itemImage = itemView.findViewById(R.id.ingredientImg);
            Log.d(TAG, "SearchViewHolder initialized");
        }

        public void loadImage(String imageUrl) {
            Log.d(TAG, "Loading image: " + imageUrl);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                itemImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.error_image)
                        .into(itemImage);
            } else {
                Log.w(TAG, "Empty image URL, hiding ImageView.");
                itemImage.setVisibility(View.GONE);
            }
        }
    }
}
