package com.example.footplanner.ui.search.presenter;

import com.example.footplanner.model.Category;
import com.example.footplanner.model.Country;
import com.example.footplanner.model.Ingredient;

import java.util.ArrayList;

public interface SearchVieww {
    void ShowIngredients(ArrayList<Ingredient> ingredients);
    void showCategories(ArrayList<Category> categories);
    void showCountries(ArrayList<Country> countries);
    void showError(String message);
}
