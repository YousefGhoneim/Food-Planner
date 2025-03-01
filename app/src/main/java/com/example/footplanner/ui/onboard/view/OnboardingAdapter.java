package com.example.footplanner.ui.onboard.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footplanner.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingItemViewHolder> {

    List<OnBoardItem> onboardingItems;

    public OnboardingAdapter(List<OnBoardItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingAdapter.OnboardingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_onboard_item, parent, false);
        return new OnboardingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingAdapter.OnboardingItemViewHolder holder, int position) {
        holder.imageOnboarding.setImageResource(onboardingItems.get(position).getImage());
        holder.textDescription.setText(onboardingItems.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    static class OnboardingItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOnboarding;
        TextView textDescription;

        public OnboardingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageOnboarding = itemView.findViewById(R.id.image_onboard);
            textDescription = itemView.findViewById(R.id.desc_txt);
        }
    }
}
