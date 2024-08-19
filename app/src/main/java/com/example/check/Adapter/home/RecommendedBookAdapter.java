package com.example.check.Adapter.home;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.check.R;
import com.example.check.model.home.recentLib.RecommendedBook;
import java.util.List;

public class RecommendedBookAdapter extends RecyclerView.Adapter<RecommendedBookAdapter.ViewHolder> {
    private List<RecommendedBook> books;

    public RecommendedBookAdapter(List<RecommendedBook> books) {
        this.books = books;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommended_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecommendedBook book = books.get(position);
        holder.bookName.setText(book.getBookname());
        holder.author.setText("▸ 저자: " + book.getAuthors());
        Glide.with(holder.itemView.getContext()).load(book.getBookimageURL()).into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookName;
        TextView author;

        public ViewHolder(View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.book_image);
            bookName = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.book_author);
        }
    }
}