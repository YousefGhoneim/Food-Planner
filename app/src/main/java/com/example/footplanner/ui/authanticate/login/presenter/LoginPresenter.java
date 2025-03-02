package com.example.footplanner.ui.authanticate.login.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.footplanner.model.Meal;
import com.example.footplanner.repo.MealRepo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenter {
    private LoginView view;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private MealRepo mealRepo;
    private CompositeDisposable disposables = new CompositeDisposable();
    private Context context;

    public LoginPresenter(LoginView view, MealRepo mealRepo, Context context) {
        this.view = view;
        this.mealRepo = mealRepo;
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void login(String email, String password) {
        view.showLoading(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    view.showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            syncDataFromFirestore(user.getUid(), () -> {
                                view.showLoginSuccess(user);
                            });
                        } else {
                            view.showLoginError("User not found");
                        }
                    } else {
                        view.showLoginError("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    public void handleGoogleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    saveUserIdToSharedPreferences(user.getUid());
                                    // Pass a Runnable as the second argument
                                    syncDataFromFirestore(user.getUid(), () -> {
                                        view.showLoginSuccess(user);
                                    });
                                }
                            } else {
                                view.showLoginError("Google login failed: " + task1.getException().getMessage());
                            }
                        });
            }
        } catch (ApiException e) {
            view.showLoginError("Google login failed: " + e.getMessage());
        }
    }

    @SuppressLint("CheckResult")
    public void syncDataFromFirestore(String userId, Runnable onComplete) {
        // Sync planned meals
        db.collection("users").document(userId).collection("planned")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("Sync", "Fetched " + queryDocumentSnapshots.size() + " planned meals from Firestore");
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String mealId = document.getId();
                        List<Long> dates = (List<Long>) document.get("dates");
                        Log.d("Sync", "Processing planned meal with ID: " + mealId);

                        // Log the document data to verify its structure
                        Log.d("Sync", "Document data: " + document.getData());

                        if (dates != null && !dates.isEmpty()) {
                            // Fetch full meal details from API
                            mealRepo.getMealById(mealId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            mealResponse -> {
                                                if (mealResponse != null && !mealResponse.getMeals().isEmpty()) {
                                                    Meal meal = mealResponse.getMeals().get(0);
                                                    Log.d("Sync", "Fetched meal details for ID: " + mealId);

                                                    // Insert meal into Room as a planned meal
                                                    mealRepo.planMeal(userId, meal, dates.get(0)) // Use the first date in the list
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(
                                                                    () -> Log.d("Sync", "Planned meal added to Room successfully!"),
                                                                    error -> Log.e("Sync", "Error inserting planned meal into Room", error)
                                                            );
                                                } else {
                                                    Log.e("Sync", "Meal response is null or empty for ID: " + mealId);
                                                }
                                            },
                                            error -> Log.e("Sync", "Error fetching meal from API", error)
                                    );
                        }
                    }
                    onComplete.run(); // Notify that synchronization is complete
                })
                .addOnFailureListener(e -> {
                    Log.e("Sync", "Error fetching planned meals", e);
                    onComplete.run(); // Notify even if there's an error
                });

        // Sync favorite meals (unchanged)
        db.collection("users").document(userId).collection("favourites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("Sync", "Fetched " + queryDocumentSnapshots.size() + " favorite meals from Firestore");
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String mealId = document.getId();
                        Log.d("Sync", "Processing favorite meal with ID: " + mealId);

                        // Fetch full meal details from API
                        mealRepo.getMealById(mealId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        mealResponse -> {
                                            if (mealResponse != null && !mealResponse.getMeals().isEmpty()) {
                                                Meal meal = mealResponse.getMeals().get(0);
                                                Log.d("Sync", "Fetched meal details for ID: " + mealId);

                                                mealRepo.toggleMealFavouriteStatus(meal, userId)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                () -> Log.d("Sync", "Favorite meal added to Room successfully!"),
                                                                error -> Log.e("Sync", "Error inserting favorite meal into Room", error)
                                                        );
                                            } else {
                                                Log.e("Sync", "Meal response is null or empty for ID: " + mealId);
                                            }
                                        },
                                        error -> Log.e("Sync", "Error fetching meal from API", error)
                                );
                    }
                })
                .addOnFailureListener(e -> Log.e("Sync", "Error fetching favorite meals", e));
    }
    private void saveUserIdToSharedPreferences(String userId) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_id", userId);
        editor.apply();
    }

    public void checkCurrentUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            saveUserIdToSharedPreferences(user.getUid()); // Save user ID
            view.navigateToHome();
        }
    }

    public void clear() {
        disposables.clear();
    }
}