package com.example.check.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.check.Adapter.detail_book.BookDetailLibraryAdapter;
import com.example.check.R;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.model.bookDetail.BookDetailModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailFragment extends Fragment {
    private static final String TAG = "BookDetailFragment";
    private static final String ARG_ISBN = "isbn";

    private ImageView bookImageView;
    private TextView bookNameTextView, authorTextView, publisherTextView, publishYearTextView, descriptionTextView;
    private RecyclerView librariesRecyclerView;
    private BookDetailLibraryAdapter libraryAdapter;

    private ApiService apiService;
    private String isbn;

    public static BookDetailFragment newInstance(String isbn) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN, isbn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient().create(ApiService.class);
        if (getArguments() != null) {
            isbn = getArguments().getString(ARG_ISBN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isbn != null) {
            loadBookDetails(isbn);
        } else {
            showToast("ISBN이 제공되지 않았습니다.");
        }
    }

    private void initializeViews(View view) {
        bookImageView = view.findViewById(R.id.bookImageView);
        bookNameTextView = view.findViewById(R.id.bookNameTextView);
        authorTextView = view.findViewById(R.id.authorTextView);
        publisherTextView = view.findViewById(R.id.publisherTextView);
        publishYearTextView = view.findViewById(R.id.publishYearTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        librariesRecyclerView = view.findViewById(R.id.librariesRecyclerView);

        if (librariesRecyclerView != null) {
            librariesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void loadBookDetails(String isbn) {
        apiService.getBookDetails(isbn).enqueue(new Callback<BookDetailModel>() {
            @Override
            public void onResponse(Call<BookDetailModel> call, Response<BookDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayBookDetail(response.body());
                } else {
                    Log.e(TAG, "Failed to fetch book details: " + response.code());
                    showToast("책 정보를 가져오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<BookDetailModel> call, Throwable t) {
                Log.e(TAG, "Error fetching book details", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    private void displayBookDetail(BookDetailModel bookDetail) {
        if (getView() == null) {
            Log.e(TAG, "Fragment view is null");
            return;
        }

        if (bookImageView != null) {
            Glide.with(this)
                    .load(bookDetail.getBookimageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(bookImageView);
        }

        if (bookNameTextView != null) bookNameTextView.setText(bookDetail.getBookname());
        if (authorTextView != null) authorTextView.setText("▸ 저자 : " + bookDetail.getAuthors());
        if (publisherTextView != null) publisherTextView.setText("▸ 출판 : " + bookDetail.getPublisher());
        if (publishYearTextView != null) publishYearTextView.setText("▸ 발행 : " + bookDetail.getPublishyear());
        if (descriptionTextView != null) descriptionTextView.setText(bookDetail.getDescription());

        if (librariesRecyclerView != null && bookDetail.getLibs() != null) {
            libraryAdapter = new BookDetailLibraryAdapter(bookDetail.getLibs());
            librariesRecyclerView.setAdapter(libraryAdapter);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}