package com.example.check.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.check.Adapter.today_book.RecommendationAdapter;
import com.example.check.R;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.fragments.todayBook.BookDetailFragment;
import com.example.check.model.today_book.Book;
import com.example.check.model.today_book.RecommendationCategory;
import com.example.check.model.today_book.RecommendationsWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodayBookFragment extends Fragment implements RecommendationAdapter.OnBookClickListener {
    private static final String TAG = "TodayBookFragment";
    private RecyclerView recyclerView;
    private RecommendationAdapter adapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_today_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        apiService = ApiClient.getClient().create(ApiService.class);
        loadRecommendations();
    }

    private void loadRecommendations() {
        apiService.getTodayBooks().enqueue(new Callback<List<RecommendationsWrapper>>() {
            @Override
            public void onResponse(Call<List<RecommendationsWrapper>> call, Response<List<RecommendationsWrapper>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<RecommendationCategory> categories = response.body().get(0).getRecommendations();
                    adapter = new RecommendationAdapter(categories, TodayBookFragment.this);
                    recyclerView.setAdapter(adapter);
                    Log.d(TAG, "Today books loaded successfully");
                } else {
                    Log.e(TAG, "Failed to fetch today books: " + response.code());
                    showToast("오늘의 책 데이터를 가져오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<List<RecommendationsWrapper>> call, Throwable t) {
                Log.e(TAG, "Error fetching today books", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    @Override
    public void onBookClick(Book book) {
        if (book != null && book.getISBN() != null) {
            Log.d(TAG, "Book clicked: " + book.getISBN());
            navigateToBookDetail(book.getISBN());
        } else {
            Log.e(TAG, "Invalid book data");
            showToast("책 정보를 불러올 수 없습니다.");
        }
    }

    private void navigateToBookDetail(String isbn) {
        BookDetailFragment fragment = BookDetailFragment.newInstance(isbn);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}