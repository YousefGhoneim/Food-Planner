package com.example.footplanner.model;

import java.util.ArrayList;

public class MealCategoryResponse {

	private ArrayList<MealSpecification> meals;
	public ArrayList<MealSpecification> getMealsFromCategory(){
		return meals;
	}
}