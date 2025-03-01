package com.example.footplanner.ui.search.presenter;

import com.example.footplanner.model.CategoryResponse;
import com.example.footplanner.model.CountryResponse;
import com.example.footplanner.model.IngredientResponse;
import com.example.footplanner.repo.MealRepo;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenter {
    private SearchVieww view;
    private MealRepo repo;
    private CompositeDisposable compositeDisposable;

    public SearchPresenter(SearchVieww view, MealRepo repo) {
        this.view = view;
        this.repo = repo;
        this.compositeDisposable = new CompositeDisposable(); // Initialize here
    }

    public void getIngredients() {
        Disposable disposable = repo.getIngredients()
                .subscribeOn(Schedulers.io())
                .map(IngredientResponse::getMeals)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::ShowIngredients, throwable -> view.showError(throwable.getMessage()));
        compositeDisposable.add(disposable);
    }

    public void getCategories() {
        Disposable disposable = repo.getCategories()
                .subscribeOn(Schedulers.io())
                .map(CategoryResponse::getCategories)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::showCategories, throwable -> view.showError(throwable.getMessage()));
        compositeDisposable.add(disposable);
    }

    public void getCountries() {
        Disposable disposable = repo.getCountries()
                .subscribeOn(Schedulers.io())
                .map(CountryResponse::getCountries)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::showCountries, throwable -> view.showError(throwable.getMessage()));
        compositeDisposable.add(disposable);
    }

    public void clear() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}