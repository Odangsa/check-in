package com.example.check.Adapter.stamp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.check.R;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private List<String> libraries;

    public LibraryAdapter(List<String> libraries) {
        this.libraries = libraries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stamp_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String library = libraries.get(position);
        if (holder.libraryImageView != null) {
            holder.libraryImageView.setImageResource(getLibraryImageResourceId(library));
        }
    }

    @Override
    public int getItemCount() {
        return libraries.size();
    }

    private int getLibraryImageResourceId(String library) {
        switch (library.toLowerCase()) {
            case "동대문도서관":
            case "용산도서관":
            case "종로도서관":
            case "양천도서관":
            case "서대문도서관":
            case "중구도서관":
            case "성동도서관":
                return R.drawable.img_ssu_library; // 실제 이미지 리소스로 교체 필요
            case "none":
                return R.drawable.icon_add;
            default:
                return R.drawable.img_cn_library; // 기본 이미지
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView libraryImageView;

        ViewHolder(View view) {
            super(view);
            libraryImageView = view.findViewById(R.id.libraryImageView);
        }
    }
}