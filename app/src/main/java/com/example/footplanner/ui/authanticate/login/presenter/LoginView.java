package com.example.footplanner.ui.authanticate.login.presenter;

import com.google.firebase.auth.FirebaseUser;

public interface LoginView {
    void showLoading(boolean isLoading);
    void showLoginSuccess(FirebaseUser user);
    void showLoginError(String message);
    void navigateToHome();
}
