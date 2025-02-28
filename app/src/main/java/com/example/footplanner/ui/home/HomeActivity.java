package com.example.footplanner.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.footplanner.AuthanticateActivity;
import com.example.footplanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.nav_view);

        userId = getUserId();

        if (userId.equals("guestUser")) {
            Toast.makeText(this, "Welcome, Guest!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Welcome back, " + userId, Toast.LENGTH_SHORT).show();
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


    }
    private String getUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_id", "guestUser");  // Default to guest user
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();  // Sign out from Firebase

        // Clear saved user ID
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Redirect to AuthActivity
        Intent intent = new Intent(HomeActivity.this, AuthanticateActivity.class);
        startActivity(intent);
        finish();
    }

}