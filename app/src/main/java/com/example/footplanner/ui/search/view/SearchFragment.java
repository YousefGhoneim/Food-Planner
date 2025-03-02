package com.example.footplanner.ui.search.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footplanner.R;
import com.example.footplanner.db.ProductLocalDataSource;
import com.example.footplanner.model.Category;
import com.example.footplanner.model.Country;
import com.example.footplanner.model.Ingredient;
import com.example.footplanner.network.ProductRemoteDataSource;
import com.example.footplanner.repo.MealRepo;
import com.example.footplanner.ui.search.presenter.SearchPresenter;
import com.example.footplanner.ui.search.presenter.SearchVieww;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;


public class SearchFragment extends Fragment implements SearchVieww {
    SearchView searchView;
    private RecyclerView searchRecyclerView;
    private ChipGroup chipGroup;
    private Chip ingredientsChip, countriesChip, categoriesChip;
    ImageView emptySearch;
    TextView searchText;
    private SearchPresenter presenter;
    private SearchAdapter searchAdapter;
    private final ArrayList<Object> allItems = new ArrayList<>();
    private final ArrayList<Object> filteredItems = new ArrayList<>();

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.searchView);
        searchRecyclerView = view.findViewById(R.id.SearchRecyclerView);
        searchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        emptySearch = view.findViewById(R.id.searchImg);
        searchText = view.findViewById(R.id.emptySearch);

        chipGroup = view.findViewById(R.id.chipGroup);
        ingredientsChip = view.findViewById(R.id.ingredientsChip);
        countriesChip = view.findViewById(R.id.countriesChip);
        categoriesChip = view.findViewById(R.id.categoriesChip);

        presenter = new SearchPresenter(this, MealRepo.getInstance(ProductLocalDataSource.getInstance(requireContext()), ProductRemoteDataSource.getInstance(requireContext())));

        searchAdapter = new SearchAdapter(getContext(), new ArrayList<>(), item ->{
            if (item instanceof Ingredient) {
                navigateToMealFregment(((Ingredient) item).getStrIngredient(), "ingredient");
            } else if (item instanceof Category) {
                navigateToMealFregment(((Category) item).getCategoryName(), "category");
            } else if (item instanceof Country) {
                navigateToMealFregment(((Country) item).getCountryName(), "country");
            }
        });
        visibility(View.GONE, View.VISIBLE);
        searchRecyclerView.setAdapter(searchAdapter);

        fetchAllData();
        setupSearchView();
        setupChipGroup();
    }

    private void fetchAllData() {
        allItems.clear();
        filteredItems.clear();
        presenter.getIngredients();
        presenter.getCategories();
        presenter.getCountries();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                if (newText.isEmpty()) {
                    visibility(View.GONE, View.VISIBLE);
                } else {
                    visibility(View.VISIBLE, View.GONE);
                }
                return true;
            }
        });
    }

    private void setupChipGroup() {
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d("SearchFragment", "Checked Chip ID: " + checkedId);
            if (checkedId == R.id.ingredientsChip) {
                Log.d("SearchFragment", "Ingredients Chip Clicked");
                presenter.getIngredients();
            } else if (checkedId == R.id.categoriesChip) {
                Log.d("SearchFragment", "Categories Chip Clicked");
                presenter.getCategories();
            } else if (checkedId == R.id.countriesChip) {
                Log.d("SearchFragment", "Countries Chip Clicked");
                presenter.getCountries();
            }
            filterData(searchView.getQuery().toString());
        });

    }

    private void filterData(String query) {
        query = query.toLowerCase();
        filteredItems.clear();

        for (Object item : allItems) {
            boolean matchesQuery = false;

            if (item instanceof Ingredient) {
                matchesQuery = ((Ingredient) item).getStrIngredient().toLowerCase().contains(query);
            } else if (item instanceof Category) {
                matchesQuery = ((Category) item).getCategoryName().toLowerCase().contains(query);
            } else if (item instanceof Country) {
                matchesQuery = ((Country) item).getCountryName().toLowerCase().contains(query);
            }

            if (matchesQuery) {
                if (ingredientsChip.isChecked() && item instanceof Ingredient) {
                    filteredItems.add(item);
                } else if (categoriesChip.isChecked() && item instanceof Category) {
                    filteredItems.add(item);
                } else if (countriesChip.isChecked() && item instanceof Country) {
                    filteredItems.add(item);
                } else if (!ingredientsChip.isChecked() && !categoriesChip.isChecked() && !countriesChip.isChecked()) {
                    filteredItems.add(item);
                }
            }
        }

        if (!filteredItems.isEmpty()) {
            searchRecyclerView.setVisibility(View.VISIBLE);
            emptySearch.setVisibility(View.GONE);
            searchText.setVisibility(View.GONE);
        } else {
            searchRecyclerView.setVisibility(View.GONE);
            emptySearch.setVisibility(View.VISIBLE);
            searchText.setVisibility(View.VISIBLE);
        }


        searchAdapter.updateData(filteredItems);
    }
    private void visibility(int visible, int gone) {
        searchRecyclerView.setVisibility(visible);
        emptySearch.setVisibility(gone);
        searchText.setVisibility(gone);
    }
    @Override
    public void ShowIngredients(ArrayList<Ingredient> ingredients) {
        allItems.addAll(ingredients); // Add ingredients without clearing
        filterData(searchView.getQuery().toString());
    }

    @Override
    public void showCategories(ArrayList<Category> categories) {
        allItems.addAll(categories); // Add categories without clearing
        filterData(searchView.getQuery().toString());
    }

    @Override
    public void showCountries(ArrayList<Country> countries) {
        allItems.addAll(countries); // Add countries without clearing
        filterData(searchView.getQuery().toString());
    }
    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();


    }

    void navigateToMealFregment(String id, String type) {
        NavController navController = Navigation.findNavController(requireView());
        SearchFragmentDirections.ActionSearchFragment2ToMealFilteringFragment action =
                SearchFragmentDirections.actionSearchFragment2ToMealFilteringFragment(id, type);
        navController.navigate(action);
    }
}