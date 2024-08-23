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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stamp_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int startIndex = position * 2;

        if (startIndex < visitedLibraries.size()) {
            holder.leftStamp.setImageResource(getLibraryImageResourceId(visitedLibraries.get(startIndex)));
            holder.leftStamp.setVisibility(View.VISIBLE);
        } else {
            holder.leftStamp.setVisibility(View.INVISIBLE);
        }

        if (startIndex + 1 < visitedLibraries.size()) {
            holder.rightStamp.setImageResource(getLibraryImageResourceId(visitedLibraries.get(startIndex + 1)));
            holder.rightStamp.setVisibility(View.VISIBLE);
            holder.arrowRight.setVisibility(View.VISIBLE);
        } else {
            holder.rightStamp.setVisibility(View.INVISIBLE);
            holder.arrowRight.setVisibility(View.INVISIBLE);
        }

        // Show diagonal arrow for all rows except the last one
        holder.arrowDiagonal.setVisibility(position < getItemCount() - 1 ? View.VISIBLE : View.INVISIBLE);

        // Show add button if it's the last stamp
        if (startIndex == visitedLibraries.size()) {
            holder.leftStamp.setImageResource(R.drawable.icon_add);
            holder.leftStamp.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (visitedLibraries.size() + 1) / 2;  // +1 to account for the add button
    }

    private int getLibraryImageResourceId(String libraryName) {
        // Implement this method to return the correct image resource for each library
        // For now, I'll return a placeholder
        return R.drawable.img_seoul_library;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView leftStamp, rightStamp, arrowRight, arrowDiagonal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftStamp = itemView.findViewById(R.id.leftStamp);
            rightStamp = itemView.findViewById(R.id.rightStamp);
            arrowRight = itemView.findViewById(R.id.arrowRight);
            arrowDiagonal = itemView.findViewById(R.id.arrowDiagonal);
        }
    }

    public void updateData(List<String> newVisitedLibraries) {
        this.visitedLibraries = newVisitedLibraries;
        notifyDataSetChanged();
    }
}