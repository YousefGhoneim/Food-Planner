package com.example.footplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {
    ViewPager2 viewPager2;
    Button button;
    List<OnBoardItem> onboardingItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_boarding);
        viewPager2 = findViewById(R.id.viewPager2);
        button = findViewById(R.id.btn_skip);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnBoardItem(R.drawable.image2, "Easily plan your meals for the week."));
        onboardingItems.add(new OnBoardItem(R.drawable.onboardimage3, "Explore a variety of delicious recipes."));
        onboardingItems.add(new OnBoardItem(R.drawable.onboardimage4, "Track your nutrition and stay fit."));
        viewPager2.setAdapter(new OnboardingAdapter(onboardingItems));
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {}).attach();
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthanticateActivity.class);
            startActivity(intent);
            finish();
        });
    }
}