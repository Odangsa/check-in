package com.example.check.fragments.todayBook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.check.Adapter.detail_book.BookDetailLibraryAdapter;
import com.example.check.R;
import com.example.check.model.bookDetail.BookDetail;
import com.example.check.model.bookDetail.Library;
import com.google.gson.Gson;
import java.util.List;

public class BookDetailFragment extends Fragment {

    private static final String ARG_BOOK_DETAIL = "book_detail";

    private ImageView bookImageView;
    private TextView bookNameTextView, authorsTextView, publisherTextView, publishYearTextView, descriptionTextView;
    private RecyclerView librariesRecyclerView;
    private BookDetailLibraryAdapter libraryAdapter;

    public static BookDetailFragment newInstance(BookDetail bookDetail) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String bookDetailJson = gson.toJson(bookDetail);
        args.putString(ARG_BOOK_DETAIL, bookDetailJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_detail_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookImageView = view.findViewById(R.id.bookImageView);
        bookNameTextView = view.findViewById(R.id.bookNameTextView);
        authorsTextView = view.findViewById(R.id.authorsTextView);
        publisherTextView = view.findViewById(R.id.publisherTextView);
        publishYearTextView = view.findViewById(R.id.publishYearTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        librariesRecyclerView = view.findViewById(R.id.librariesRecyclerView);

        librariesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            String bookDetailJson = getArguments().getString(ARG_BOOK_DETAIL);
            Gson gson = new Gson();
            BookDetail bookDetail = gson.fromJson(bookDetailJson, BookDetail.class);
            bindBookDetails(bookDetail);
        }
    }

    private void bindBookDetails(BookDetail bookDetail) {
        Glide.with(this)
                .load(bookDetail.getBookimageURL())
                .into(bookImageView);

        bookNameTextView.setText(bookDetail.getBookname());
        authorsTextView.setText(bookDetail.getAuthors());
        publisherTextView.setText(bookDetail.getPublisher());
        publishYearTextView.setText(bookDetail.getPublishyear());
        descriptionTextView.setText(bookDetail.getDescription());

        List<Library> libraries = bookDetail.getLibs();
        libraryAdapter = new BookDetailLibraryAdapter(libraries);
        librariesRecyclerView.setAdapter(libraryAdapter);
    }
}