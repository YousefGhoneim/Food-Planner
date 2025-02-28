package com.example.footplanner.model;

import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("strArea")
    String CountryName;;
    public Country(String countryName) {
        CountryName = countryName;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }




}
