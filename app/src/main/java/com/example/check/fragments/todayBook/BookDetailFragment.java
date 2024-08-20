package com.example.check.fragments.todayBook;

import android.os.Bundle;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class BookDetailFragment extends Fragment {

    private ImageView bookImageView;
    private TextView bookNameTextView, authorsTextView, publisherTextView, publishYearTextView, descriptionTextView;
    private RecyclerView librariesRecyclerView;
    private BookDetailLibraryAdapter libraryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 레이아웃을 fragment_book_detail_screen으로 변경
        return inflater.inflate(R.layout.fragment_book_detail_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View 초기화
        bookImageView = view.findViewById(R.id.bookImageView);
        bookNameTextView = view.findViewById(R.id.bookNameTextView);
        authorsTextView = view.findViewById(R.id.authorsTextView);
        publisherTextView = view.findViewById(R.id.publisherTextView);
        publishYearTextView = view.findViewById(R.id.publishYearTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        librariesRecyclerView = view.findViewById(R.id.librariesRecyclerView);

        // RecyclerView 설정
        librariesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // JSON 파일에서 BookDetail 데이터를 읽어와 UI에 바인딩
        loadBookDetailFromJson();
    }

    private void loadBookDetailFromJson() {
        try {
            // raw/book_detail.json 파일 읽기
            InputStream is = getResources().openRawResource(R.raw.book_detail);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            is.close();

            // JSON 문자열을 BookDetail 객체로 변환
            Gson gson = new Gson();
            BookDetail bookDetail = gson.fromJson(jsonString.toString(), BookDetail.class);

            // UI에 데이터 바인딩
            bindBookDetails(bookDetail);

        } catch (Exception e) {
            Log.e("BookDetailFragment", "Error reading JSON file", e);
        }
    }

    private void bindBookDetails(BookDetail bookDetail) {
        // 책 이미지 로드
        Glide.with(this)
                .load(bookDetail.getBookimageURL())
                .into(bookImageView);

        // 텍스트 데이터 설정
        bookNameTextView.setText(bookDetail.getBookname());
        authorsTextView.setText(bookDetail.getAuthors());
        publisherTextView.setText(bookDetail.getPublisher());
        publishYearTextView.setText(bookDetail.getPublishyear());
        descriptionTextView.setText(bookDetail.getDescription());

        // RecyclerView에 도서관 데이터 바인딩
        List<Library> libraries = bookDetail.getLibs();
        libraryAdapter = new BookDetailLibraryAdapter(libraries);
        librariesRecyclerView.setAdapter(libraryAdapter);
    }
}
