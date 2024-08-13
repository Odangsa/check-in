package com.example.check.Adapter.detail_book;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.check.R;
import com.example.check.model.bookDetail.Library;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {
    private List<Library> libraries;

    public LibraryAdapter(List<Library> libraries) {
        this.libraries = libraries;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        Library library = libraries.get(position);
        holder.libNameTextView.setText(library.getLibname());
        holder.distanceTextView.setText(library.getDistance() != null ? library.getDistance() + "km" : "거리 정보 없음");
    }

    @Override
    public int getItemCount() {
        return libraries.size();
    }

    static class LibraryViewHolder extends RecyclerView.ViewHolder {
        TextView libNameTextView;
        TextView distanceTextView;

        LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            libNameTextView = itemView.findViewById(R.id.libNameTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
        }
    }
}