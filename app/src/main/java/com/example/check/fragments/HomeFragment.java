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
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import com.bumptech.glide.Glide;
import com.example.check.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private LinearLayout recentLibrariesContainer;
    private LinearLayout recommendedBooksContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home1, container, false);

        recentLibrariesContainer = view.findViewById(R.id.recent_libraries_container);
        recommendedBooksContainer = view.findViewById(R.id.recommended_books_container);

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

        // 테스트 메서드 호출
        testDataLoading();

        return view;
    }

    private void loadRecentLibraries() {
        Log.d(TAG, "Loading recent libraries");
        try {
            String jsonString = loadJSONFromRaw(R.raw.lib);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("recent_libraries");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject libraryObject = jsonArray.getJSONObject(i);
                String name = libraryObject.getString("library");
                int visitCount = libraryObject.getInt("visit_count");

                addLibraryView(name, visitCount);
            }
            Log.d(TAG, "Recent libraries loaded successfully");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing recent libraries JSON", e);
            showToast("최근 도서관 데이터 파싱 중 오류가 발생했습니다.");
        }
    }

    private void loadRecommendedBooks() {
        Log.d(TAG, "Loading recommended books");
        try {
            String jsonString = loadJSONFromRaw(R.raw.recommendation_book_two);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("books");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bookObject = jsonArray.getJSONObject(i);
                String bookname = bookObject.getString("bookname");
                String authors = bookObject.getString("authors");
                String bookimageURL = bookObject.getString("bookimageURL");

                addBookView(bookname, authors, bookimageURL);
            }
            Log.d(TAG, "Recommended books loaded successfully");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing recommended books JSON", e);
            showToast("추천 도서 데이터 파싱 중 오류가 발생했습니다.");
        }
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

    private String loadJSONFromRaw(int resourceId) {
        try {
            InputStream is = getResources().openRawResource(resourceId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Error loading JSON from raw resource", e);
            return null;
        }
    }

    private void performSearch(String query) {
        // TODO: Implement search functionality
        Log.d(TAG, "Performing search with query: " + query);
    }

    private void testDataLoading() {
        Log.d(TAG, "Starting data loading test");

        // 최근 도서관 데이터 로드 테스트
        String recentLibrariesJson = loadJSONFromRaw(R.raw.lib);
        if (recentLibrariesJson != null) {
        } else {
            Log.e(TAG, "Failed to load recent libraries JSON");
        }

        // 추천 도서 데이터 로드 테스트
        String recommendedBooksJson = loadJSONFromRaw(R.raw.recommendation_book_two);
        if (recommendedBooksJson != null) {
        } else {
            Log.e(TAG, "Failed to load recommended books JSON");
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}