package com.example.check.Adapter.detail_book;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.check.R;
import com.example.check.model.bookDetail.LibraryModel;

import java.util.List;

public class BookDetailLibraryAdapter extends RecyclerView.Adapter<BookDetailLibraryAdapter.LibraryViewHolder> {

    private List<LibraryModel> libraries;

    public BookDetailLibraryAdapter(List<LibraryModel> libraries) {
        this.libraries = libraries;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_detail_library, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        LibraryModel library = libraries.get(position);
        holder.bind(library);
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

        void bind(LibraryModel library) {
            libNameTextView.setText(library.getLibname());
            String distance = library.getDistance();
            if (!"None".equals(distance)) {
                distanceTextView.setText(String.format("%s km", distance));
            } else {
                distanceTextView.setText("거리 정보 없음");
            }
        }
    }
}