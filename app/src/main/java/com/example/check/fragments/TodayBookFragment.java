package com.example.check.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.check.Adapter.today_book.RecommendationAdapter;
import com.example.check.R;
import com.example.check.fragments.todayBook.BookDetailFragment;
import com.example.check.model.bookDetail.BookDetail;
import com.example.check.model.today_book.Book;
import com.example.check.model.today_book.RecommendationCategory;
import com.example.check.api.ApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TodayBookFragment extends Fragment implements RecommendationAdapter.OnBookClickListener {
    private static final String TAG = "TodayBookFragment";
    private RecyclerView recyclerView;
    private RecommendationAdapter adapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.activity_today_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        recyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupApiService();
        loadRecommendations();
    }

    private void setupApiService() {
        Log.d(TAG, "Setting up ApiService");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // 에뮬레이터용
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void loadRecommendations() {
        Log.d(TAG, "Loading recommendations");
        apiService.getRecommendations().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Recommendations response received");
                    String responseBody = response.body();
                    Log.d(TAG, "Response body: " + responseBody);
                    List<RecommendationCategory> categories = parseRecommendations(responseBody);
                    if (categories.isEmpty()) {
                        Log.w(TAG, "No recommendations found");
                    } else {
                        Log.d(TAG, "Parsed " + categories.size() + " recommendation categories");
                        showData(categories);
                    }
                } else {
                    Log.e(TAG, "Error loading recommendations: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Failed to load recommendations", t);
            }
        });
    }

    private List<RecommendationCategory> parseRecommendations(String jsonString) {
        List<RecommendationCategory> categories = new ArrayList<>();
        try {
            Log.d(TAG, "Parsing recommendations JSON");
            JSONArray jsonArray = new JSONArray(jsonString);
            Log.d(TAG, "JSON array length: " + jsonArray.length());
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONArray recommendationsArray = jsonObject.getJSONArray("recommendations");
            Log.d(TAG, "Recommendations array length: " + recommendationsArray.length());

            Gson gson = new Gson();
            Type categoryType = new TypeToken<RecommendationCategory>(){}.getType();

            for (int i = 0; i < recommendationsArray.length(); i++) {
                JSONObject categoryObject = recommendationsArray.getJSONObject(i);
                if (!categoryObject.has("recommendationTitle")) {
                    categoryObject.put("recommendationTitle", categoryObject.getString("type"));
                }
                RecommendationCategory category = gson.fromJson(categoryObject.toString(), categoryType);
                categories.add(category);
                Log.d(TAG, "Parsed category: " + category.getRecommendationTitle());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing recommendations JSON", e);
        }
        return categories;
    }

    private void showData(List<RecommendationCategory> categories) {
        Log.d(TAG, "Showing data: " + categories.size() + " categories");
        adapter = new RecommendationAdapter(categories, this);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set to RecyclerView");
    }

    @Override
    public void onBookClick(Book book) {
        Log.d(TAG, "Book clicked: " + book.getISBN());
        fetchBookDetails(book.getISBN());
    }

    private void fetchBookDetails(String isbn) {
        Log.d(TAG, "Fetching book details for ISBN: " + isbn);
        apiService.getBookDetail(isbn).enqueue(new Callback<BookDetail>() {
            @Override
            public void onResponse(Call<BookDetail> call, Response<BookDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Book details fetched successfully");
                    BookDetail bookDetail = response.body();
                    navigateToBookDetail(bookDetail);
                } else {
                    Log.e(TAG, "Error fetching book details: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BookDetail> call, Throwable t) {
                Log.e(TAG, "Failed to fetch book details", t);
            }
        });
    }

    private void navigateToBookDetail(BookDetail bookDetail) {
        Log.d(TAG, "Navigating to BookDetailFragment");
        BookDetailFragment fragment = BookDetailFragment.newInstance(bookDetail);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}