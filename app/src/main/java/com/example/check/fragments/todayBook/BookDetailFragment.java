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
import com.example.check.Adapter.detail_book.LibraryAdapter;
import com.example.check.R;
import com.example.check.model.bookDetail.BookDetail;

import java.io.Serializable;

public class BookDetailFragment extends Fragment {
    private BookDetail bookDetail;

    // View 요소들을 선언합니다.
    private ImageView bookImageView;
    private TextView bookNameTextView, authorsTextView, publisherTextView, publishYearTextView, descriptionTextView;
    private RecyclerView librariesRecyclerView;
    private LibraryAdapter libraryAdapter;

    public static BookDetailFragment newInstance(BookDetail bookDetail) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("book_detail", (Serializable) bookDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookDetail = (BookDetail) getArguments().getSerializable("book_detail");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View 요소들을 초기화합니다.
        bookImageView = view.findViewById(R.id.bookImageView);
        bookNameTextView = view.findViewById(R.id.bookNameTextView);
        authorsTextView = view.findViewById(R.id.authorsTextView);
        publisherTextView = view.findViewById(R.id.publisherTextView);
        publishYearTextView = view.findViewById(R.id.publishYearTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        librariesRecyclerView = view.findViewById(R.id.librariesRecyclerView);

        // 데이터를 뷰에 설정합니다.
        if (bookDetail != null) {
            Glide.with(this).load(bookDetail.getBookimageURL()).into(bookImageView);
            bookNameTextView.setText(bookDetail.getBookname());
            authorsTextView.setText("저자 : " + bookDetail.getAuthors());
            publisherTextView.setText("출판 : " + bookDetail.getPublisher());
            publishYearTextView.setText("발행 : " + bookDetail.getPublishyear());
            descriptionTextView.setText(bookDetail.getDescription());

            // RecyclerView 설정
            libraryAdapter = new LibraryAdapter(bookDetail.getLibs());
            librariesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            librariesRecyclerView.setAdapter(libraryAdapter);
        }
    }
}