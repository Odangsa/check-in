package com.example.check.fragments.todayBook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.check.R;
import com.example.check.api.ApiService;
import com.example.check.model.bookDetail.BookDetail;
import com.example.check.model.bookDetail.Library;

import java.util.List;

public class BookDetailFragment extends Fragment {
    private static final String TAG = "BookDetailFragment";
    private static final String ARG_ISBN = "isbn";

    private ImageView bookImageView;
    private TextView bookNameTextView, authorTextView, publisherTextView, publishYearTextView, descriptionTextView;
    private LinearLayout librariesContainer;

    private ApiService apiService;

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
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        String isbn = getArguments() != null ? getArguments().getString(ARG_ISBN) : null;


    }


    private void initializeViews(View view) {
        bookImageView = view.findViewById(R.id.bookImageView);
        bookNameTextView = view.findViewById(R.id.bookNameTextView);
        authorTextView = view.findViewById(R.id.authorTextView);
        publisherTextView = view.findViewById(R.id.publisherTextView);
        publishYearTextView = view.findViewById(R.id.publishYearTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        librariesContainer = view.findViewById(R.id.librariesContainer);
    }



    private void displayBookDetail(BookDetail bookDetail) {
        Glide.with(this)
                .load(bookDetail.getBookimageURL())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(bookImageView);

        bookNameTextView.setText(bookDetail.getBookname());
        authorTextView.setText("▸ 저자 : " + bookDetail.getAuthors());
        publisherTextView.setText("▸ 출판 : " + bookDetail.getPublisher());
        publishYearTextView.setText("▸ 발행 : " + bookDetail.getPublishyear());
        descriptionTextView.setText(bookDetail.getDescription());

        displayLibraries(bookDetail.getLibs());
    }

    private void displayLibraries(List<Library> libraries) {
        librariesContainer.removeAllViews();
        for (Library library : libraries) {
            View libraryView = getLayoutInflater().inflate(R.layout.item_library, librariesContainer, false);

            TextView libNameTextView = libraryView.findViewById(R.id.libNameTextView);
            TextView distanceTextView = libraryView.findViewById(R.id.distanceTextView);

            libNameTextView.setText(library.getLibname());

            Integer distance = library.getDistance();
            String distanceText = distance != null ?
                    String.format("%.1f km", distance / 1000.0) : "거리 정보 없음";
            distanceTextView.setText(distanceText);

            librariesContainer.addView(libraryView);
        }
    }

    private void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error: " + message);
    }
}