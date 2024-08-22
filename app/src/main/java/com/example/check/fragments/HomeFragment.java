package com.example.check.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.check.MainActivity;
import com.example.check.R;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.model.home.RecentLibrary;
import com.example.check.model.home.RecommendedBook;
import com.example.check.model.home.RecommendedBooksWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private LinearLayout recentLibrariesContainer;
    private LinearLayout recommendedBooksContainer;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recentLibrariesContainer = view.findViewById(R.id.recent_libraries_container);
        recommendedBooksContainer = view.findViewById(R.id.recommended_books_container);

        apiService = ApiClient.getClient().create(ApiService.class);

        loadRecentLibraries();
        loadRecommendedBooks();

        SearchView searchView = view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    private void loadRecentLibraries() {
        apiService.getRecentLibraries(MainActivity.userId).enqueue(new Callback<List<RecentLibrary>>() {
            @Override
            public void onResponse(Call<List<RecentLibrary>> call, Response<List<RecentLibrary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RecentLibrary> libraries = response.body();
                    for (RecentLibrary library : libraries) {
                        addLibraryView(library.getLibrary(), library.getVisitCount());
                    }
                    Log.d(TAG, "Recent libraries loaded successfully");
                } else {
                    Log.e(TAG, "Failed to fetch recent libraries: " + response.code());
                    showToast("최근 도서관 데이터를 가져오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<List<RecentLibrary>> call, Throwable t) {
                Log.e(TAG, "Error fetching recent libraries", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    private void loadRecommendedBooks() {
        apiService.getRecommendedBooks().enqueue(new Callback<RecommendedBooksWrapper>() {
            @Override
            public void onResponse(Call<RecommendedBooksWrapper> call, Response<RecommendedBooksWrapper> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RecommendedBook> books = response.body().getBooks();
                    for (RecommendedBook book : books) {
                        addBookView(book.getBookname(), book.getAuthors(), book.getBookimageURL());
                    }
                    Log.d(TAG, "Recommended books loaded successfully");
                } else {
                    Log.e(TAG, "Failed to fetch recommended books: " + response.code());
                    showToast("추천 도서 데이터를 가져오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RecommendedBooksWrapper> call, Throwable t) {
                Log.e(TAG, "Error fetching recommended books", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    private void addLibraryView(String name, int visitCount) {
        View libraryView = getLayoutInflater().inflate(R.layout.item_home_recent_library, recentLibrariesContainer, false);

        ImageView libraryImage = libraryView.findViewById(R.id.library_image);
        TextView libraryName = libraryView.findViewById(R.id.library_name);
        TextView visitCountView = libraryView.findViewById(R.id.visit_count);

        libraryImage.setImageResource(R.drawable.img_cn_library);
        libraryName.setText(name);
        visitCountView.setText("방문횟수: " + visitCount);

        recentLibrariesContainer.addView(libraryView);
    }

    private void addBookView(String bookname, String authors, String bookimageURL) {
        View bookView = getLayoutInflater().inflate(R.layout.item_recommended_book, recommendedBooksContainer, false);

        ImageView bookImage = bookView.findViewById(R.id.book_image);
        TextView bookNameView = bookView.findViewById(R.id.book_name);
        TextView authorView = bookView.findViewById(R.id.book_author);

        Glide.with(this).load(bookimageURL).into(bookImage);
        bookNameView.setText(bookname);
        authorView.setText("▸ 저자: " + authors);

        recommendedBooksContainer.addView(bookView);

        if (recommendedBooksContainer.getChildCount() > 1) {
            View divider = new View(getContext());
            divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(getResources().getColor(R.color.gray));
            recommendedBooksContainer.addView(divider);
        }
    }

    private void performSearch(String query) {
        // TODO: Implement search functionality
        Log.d(TAG, "Performing search with query: " + query);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}