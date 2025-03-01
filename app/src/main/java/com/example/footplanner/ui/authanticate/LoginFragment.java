package com.example.footplanner.ui.authanticate;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footplanner.R;
import com.example.footplanner.ui.home.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {
    private Button loginButton, googleLogin, facebookLogin;
    private TextView emailInput, passwordInput, signupRedirect, forgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginFragment";

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginButton = view.findViewById(R.id.loginButton);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        signupRedirect = view.findViewById(R.id.signupRedirect);
        googleLogin = view.findViewById(R.id.googleLogin);
        facebookLogin = view.findViewById(R.id.facebookLogin);
        forgotPassword = view.findViewById(R.id.forgotPassword);
        progressBar = view.findViewById(R.id.loginProgressBar);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), options);

        signupRedirect.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_loginFragment2_to_registerFragment)
        );

        loginButton.setOnClickListener(v -> login());

        googleLogin.setOnClickListener(v -> {
            Intent i = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(i,1234);
        });
    }

    private void login() {
        String email = emailInput != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput != null ? passwordInput.getText().toString().trim() : "";

        if (!validateInput(email, password)) return;
        toggleLoading(true);
        signIn(email, password);
    }

    private void signIn(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    toggleLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(requireContext(), "Login successful", LENGTH_SHORT).show();
                        updateUI(user);
                    } else {
                        Toast.makeText(requireContext(), "Login failed", LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            saveUserId(mAuth.getCurrentUser().getUid());
            startActivity(intent);
            requireActivity().finish();
        } else {
            Toast.makeText(requireContext(), "Login failed", LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void reload() {
        Intent intent = new Intent(requireContext(), HomeActivity.class);
        saveUserId(mAuth.getCurrentUser().getUid());
        startActivity(intent);
        requireActivity().finish();
    }

    private boolean validateInput(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid Email");
            return false;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    saveUserId(mAuth.getCurrentUser().getUid());
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }

    }
    private void saveUserId(String userId) {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_id", userId);
        editor.apply();
    }
}
