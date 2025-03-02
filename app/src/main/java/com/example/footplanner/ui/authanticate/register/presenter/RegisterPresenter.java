package com.example.footplanner.ui.authanticate.register.presenter;

import android.content.Context;
import android.util.Log;

import com.example.footplanner.repo.MealRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterPresenter {
    private RegisterView view;
    private FirebaseAuth auth;
    private MealRepo mealRepo;
    private Context context;

    public RegisterPresenter(RegisterView view, MealRepo mealRepo, Context context) {
        this.view = view;
        this.mealRepo = mealRepo;
        this.auth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void registerUser(String email, String password) {
        view.showLoading(true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    view.showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            sendEmailVerification(user);
                        } else {
                            view.showRegistrationError("User not found");
                        }
                    } else {
                        view.showRegistrationError("Registration failed: " + task.getException().getMessage());
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(emailTask -> {
                    if (emailTask.isSuccessful()) {
                        view.showRegistrationSuccess(user);
                        auth.signOut(); // Sign out after registration
                        view.navigateToLogin();
                    } else {
                        Log.e("RegisterPresenter", "Email Verification Error", emailTask.getException());
                        view.showRegistrationError("Failed to send verification email");
                    }
                });
    }

    public void clear() {
        // Clear any resources if needed
    }
}