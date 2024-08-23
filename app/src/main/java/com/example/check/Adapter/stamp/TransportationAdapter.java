package com.example.check.Adapter.stamp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.check.R;

import java.util.List;

public class TransportationAdapter extends RecyclerView.Adapter<TransportationAdapter.ViewHolder> {

    private List<String> visitedLibraries;

    public TransportationAdapter(List<String> visitedLibraries) {
        this.visitedLibraries = visitedLibraries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stamp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < visitedLibraries.size()) {
            String libraryName = visitedLibraries.get(position);
            holder.libraryImageView.setImageResource(getLibraryImageResourceId(libraryName));
            holder.libraryImageView.setVisibility(View.VISIBLE);
            holder.addStampButton.setVisibility(View.GONE);
        } else {
            holder.libraryImageView.setVisibility(View.GONE);
            holder.addStampButton.setVisibility(View.VISIBLE);
        }

        // Set arrow visibility
        if (position % 3 == 2 && position < visitedLibraries.size() - 1) {
            holder.arrowRight.setVisibility(View.VISIBLE);
        } else {
            holder.arrowRight.setVisibility(View.GONE);
        }

        if (position > 0 && position % 3 == 0) {
            holder.arrowDown.setVisibility(View.VISIBLE);
        } else {
            holder.arrowDown.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return visitedLibraries.size() + 1; // +1 for the add stamp button
    }

    private int getLibraryImageResourceId(String libraryName) {
        // Implement this method to return the correct image resource for each library
        // For now, I'll return a placeholder
        return R.drawable.img_seoul_library;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView libraryImageView;
        ImageView addStampButton;
        ImageView arrowRight;
        ImageView arrowDown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            libraryImageView = itemView.findViewById(R.id.libraryImageView);
            addStampButton = itemView.findViewById(R.id.addStampButton);
            arrowRight = itemView.findViewById(R.id.arrowRight);
            arrowDown = itemView.findViewById(R.id.arrowDown);
        }
    }

    public void updateData(List<String> newVisitedLibraries) {
        this.visitedLibraries = newVisitedLibraries;
        notifyDataSetChanged();
    }
}