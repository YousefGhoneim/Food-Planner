package com.example.footplanner.ui.onboard.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.footplanner.ui.authanticate.AuthanticateActivity;
import com.example.footplanner.R;
import com.example.footplanner.ui.onboard.presenter.OnboardingPresenter;
import com.example.footplanner.ui.onboard.presenter.OnboardingView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity implements OnboardingView {
    private OnboardingPresenter presenter;
    private ViewPager2 viewPager;
    private Button btnSkip;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new OnboardingPresenter(this, this);
        presenter.checkFirstTimeLaunch(); // Check if onboarding is needed
    }

    @Override
    public void showOnboarding() {
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.viewPager2);
        btnSkip = findViewById(R.id.btn_skip);
        tabLayout = findViewById(R.id.tabLayout);

        List<OnBoardItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnBoardItem(R.drawable.onboardimage4, "Welcome to FootPlanner!"));
        onboardingItems.add(new OnBoardItem(R.drawable.image2, "Plan your meals effortlessly!"));
        onboardingItems.add(new OnBoardItem(R.drawable.onboardimage3, "Get started now!"));

        OnboardingAdapter adapter = new OnboardingAdapter(onboardingItems);
        viewPager.setAdapter(adapter);

        btnSkip.setOnClickListener(v -> {
            presenter.setFirstTimeLaunchFalse();
            navigateToMainScreen();
        });
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();
        tabLayout.setTabTextColors(getResources().getColor(R.color.background_dark),
                getResources().getColor(R.color.primary));
    }

    @Override
    public void navigateToMainScreen() {
        startActivity(new Intent(OnBoardingActivity.this, AuthanticateActivity.class));
        finish();
    }
}
