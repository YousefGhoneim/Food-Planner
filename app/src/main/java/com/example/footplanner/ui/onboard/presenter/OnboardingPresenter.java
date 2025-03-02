package com.example.footplanner.ui.onboard.presenter;

import android.content.Context;

public class OnboardingPresenter {
    private OnboardingView view;
    private OnboardingRepository repository;

    public OnboardingPresenter(OnboardingView view, Context context) {
        this.view = view;
        this.repository = new OnboardingRepository(context);
    }

    public void checkFirstTimeLaunch() {
        if (repository.isFirstTimeLaunch()) {
            view.showOnboarding();
        } else {
            view.navigateToMainScreen();
        }
    }

    public void setFirstTimeLaunchFalse() {
        repository.setFirstTimeLaunch(false);
    }
}
