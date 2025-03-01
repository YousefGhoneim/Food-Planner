package com.example.footplanner.ui.authanticate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {
    private EditText emailInput, passwordInput, confirmPasswordInput;
    private TextView loginRedirect;
    private Button signupButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private EditText nameInput;

    private static final String TAG = "RegisterFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Auth
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI Elements
        nameInput = view.findViewById(R.id.etName);
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

        if (!validateInput(email, password, confirmPassword)) return;

        registerUser(email, password);
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

    private void registerUser(String email, String password) {
        toggleLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(emailTask -> {
                                if (emailTask.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Check your email for verification", Toast.LENGTH_LONG).show();
                                    mAuth.signOut(); // Sign out after registration
                                    Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_loginFragment2);
                                    toggleLoading(false);
                                } else {
                                    Log.e(TAG, "Email Verification Error", emailTask.getException());
                                }
                            });
                }
            } else {
                Log.e(TAG, "Registration Error", task.getException());
                Toast.makeText(getActivity(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
            toggleLoading(false);
        });
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        signupButton.setEnabled(!isLoading);
    }
}
