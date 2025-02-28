package com.example.footplanner.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class IngredientResponse{

	@SerializedName("meals")
	private ArrayList<Ingredient> meals;

	public void setMeals(ArrayList<Ingredient> meals){
		this.meals = meals;
	}

	public ArrayList<Ingredient> getMeals(){
		return meals;
	}
}