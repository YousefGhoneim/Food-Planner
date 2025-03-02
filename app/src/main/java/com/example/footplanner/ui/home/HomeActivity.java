package com.example.footplanner.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.footplanner.R;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;
import com.example.footplanner.ui.authanticate.AuthanticateActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements HomeContract {
    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;
    private String userId;
    private FloatingActionButton menuButton;
    private LinearLayout menuLayout;
    private ImageView logoutIcon;
    private HomePresenter presenter;
    private MealRepo mealRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        menuButton = findViewById(R.id.menuButton);
        menuLayout = findViewById(R.id.menuLayout);
        logoutIcon = findViewById(R.id.logoutIcon);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = findViewById(R.id.nav_view);

        userId = getUserId();

        if (userId.equals("guestUser")) {
            Toast.makeText(this, "Welcome, Guest!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Welcome back, " + userId, Toast.LENGTH_SHORT).show();
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        menuButton.setOnClickListener(v -> toggleMenu());

        mealRepo = MealRepo.getInstance(
                ProductLocalDataSource.getInstance(this),
                ProductRemoteDataSource.getInstance(this)
        );
        presenter = new HomePresenter(this, mealRepo, this);

        // Handle Logout Icon Click
        logoutIcon.setOnClickListener(v -> presenter.logout());
    }

    private String getUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_id", "guestUser");
    }

    private void toggleMenu() {
        if (menuLayout.getVisibility() == View.GONE) {
            menuLayout.setVisibility(View.VISIBLE); // Show the menu
        } else {
            menuLayout.setVisibility(View.GONE); // Hide the menu
        }
    }

    @Override
    public void onUserMealsCleared() {
        Toast.makeText(this, "User meals cleared successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogoutSuccess() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, AuthanticateActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocalDatabaseCleared() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, AuthanticateActivity.class);
        startActivity(intent);
        finish();    }

    @Override
    public void switchFragment(int position) {

    }

    @Override
    public void onNavigationItemSelected(int newIndex) {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.clear();
    }
}