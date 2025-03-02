package com.example.footplanner.ui.authanticate.register.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footplanner.R;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;
import com.example.footplanner.ui.authanticate.register.presenter.RegisterPresenter;
import com.example.footplanner.ui.authanticate.register.presenter.RegisterView;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment implements RegisterView {
    private EditText emailInput, passwordInput, confirmPasswordInput;
    private TextView loginRedirect;
    private Button signupButton;
    private ProgressBar progressBar;
    private RegisterPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Presenter
        presenter = new RegisterPresenter(this, MealRepo.getInstance(ProductLocalDataSource.getInstance(requireContext()), ProductRemoteDataSource.getInstance(requireContext())), requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI Elements
        emailInput = view.findViewById(R.id.etEmail);
        passwordInput = view.findViewById(R.id.etPassword);
        confirmPasswordInput = view.findViewById(R.id.etConfirmPassword);
        signupButton = view.findViewById(R.id.btnRegister);
        loginRedirect = view.findViewById(R.id.tvLogin);
        progressBar = view.findViewById(R.id.progressBar);

        // Navigate to Login Fragment
        loginRedirect.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment2));

        // Handle Signup
        signupButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (validateInput(email, password, confirmPassword)) {
            presenter.registerUser(email, password);
        }
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid Email");
            return false;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        signupButton.setEnabled(!isLoading);
    }

    @Override
    public void showRegistrationSuccess(FirebaseUser user) {
        Toast.makeText(requireContext(), "Check your email for verification", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showRegistrationError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToLogin() {
        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_loginFragment2);
    }
}