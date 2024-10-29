package com.example.check.Adapter.stamp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.check.R;

import java.util.List;

public class StampBoardAdapter extends RecyclerView.Adapter<StampBoardAdapter.ViewHolder> {
    private List<String> libraries;

    public StampBoardAdapter(List<String> libraries) {
        this.libraries = libraries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_stamp_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int startIndex = position * 2;
        if (startIndex < libraries.size()) {
            holder.bind(libraries.get(startIndex), startIndex + 1 < libraries.size() ? libraries.get(startIndex + 1) : null);
        }
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil(libraries.size() / 2.0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView libraryName1, libraryName2;
        ImageView stampImage1, stampImage2;

        ViewHolder(View itemView) {
            super(itemView);
            libraryName1 = itemView.findViewById(R.id.libraryName1);
            libraryName2 = itemView.findViewById(R.id.libraryName2);
            stampImage1 = itemView.findViewById(R.id.stampImage1);
            stampImage2 = itemView.findViewById(R.id.stampImage2);
        }

        void bind(String library1, String library2) {
            libraryName1.setText(library1);
            stampImage1.setVisibility(View.VISIBLE);

            if (library2 != null) {
                libraryName2.setText(library2);
                stampImage2.setVisibility(View.VISIBLE);
            } else {
                libraryName2.setText("");
                stampImage2.setVisibility(View.INVISIBLE);
            }
        }
    }
}