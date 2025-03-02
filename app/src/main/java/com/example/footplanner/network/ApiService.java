package com.example.footplanner.network;

import com.example.footplanner.model.CategoryResponse;
import com.example.footplanner.model.CountryResponse;
import com.example.footplanner.model.IngredientResponse;
import com.example.footplanner.model.MealCategoryResponse;
import com.example.footplanner.model.MealCountryResponse;
import com.example.footplanner.model.MealIngredientResponse;
import com.example.footplanner.model.MealResponse;
import com.example.footplanner.model.MealSpecification;

import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

    @GET("categories.php")
    Single<CategoryResponse> getMealCategories();

    @GET("random.php")
    Single<MealResponse> getRandomMeal();

    @GET("list.php?a=list")
    Single<CountryResponse> getMealCountries();

    @GET("list.php?i=list")
    Single<IngredientResponse> getMealIngredients();

    @GET("filter.php")
    Single<MealCategoryResponse> getMealsByCategory(@Query("c") String category);

    @GET("filter.php")
    Single<MealCountryResponse> getMealsByCountry(@Query("a") String country); // Updated naming

    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String id);

    @GET("filter.php")
    Single<MealIngredientResponse> getMealsByIngredient(@Query("i") String ingredient);

    @GET("search.php")
    Single<MealResponse> getMealByName(@Query("s") String name);

    @GET("filter.php")
    Single<MealCountryResponse> getMealsByArea(@Query("a") String area); // Fixed incorrect return type

    @GET("search.php")
    Single<MealResponse> getMealByFirstLetter(@Query("f") String letter);

    @GET
    Single<ResponseBody> getIngredientsImage(@Url String imageUrl);
}
