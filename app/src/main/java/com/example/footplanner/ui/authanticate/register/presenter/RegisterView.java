package com.example.footplanner.ui.authanticate.register.presenter;

import com.google.firebase.auth.FirebaseUser;

public interface RegisterView {
    void showLoading(boolean isLoading);
    void showRegistrationSuccess(FirebaseUser user);
    void showRegistrationError(String message);
    void navigateToLogin();
}
