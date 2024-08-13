// RecommendationAdapter.java
package com.example.check.Adapter.today_book;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.check.R;
import com.example.check.model.today_book.Book;
import com.example.check.model.today_book.RecommendationCategory;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {
    private List<RecommendationCategory> categories;
    private OnBookClickListener listener;

    public RecommendationAdapter(List<RecommendationCategory> categories, OnBookClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation_category, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        RecommendationCategory category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class RecommendationViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTitleTextView;
        private RecyclerView booksRecyclerView;

        RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitleTextView = itemView.findViewById(R.id.categoryTitleTextView);
            booksRecyclerView = itemView.findViewById(R.id.booksRecyclerView);
            booksRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        void bind(RecommendationCategory category) {
            categoryTitleTextView.setText(category.getRecommendationTitle());
            BookAdapter bookAdapter = new BookAdapter(category.getBooks(), listener);
            booksRecyclerView.setAdapter(bookAdapter);
        }
    }

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }
}