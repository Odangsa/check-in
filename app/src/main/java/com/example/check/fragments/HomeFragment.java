package com.example.check.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

        return view;
    }

    private void loadRecentLibraries() {
        try {
            String jsonString = loadJSONFromRaw(R.raw.lib);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("recent_libraries");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject libraryObject = jsonArray.getJSONObject(i);
                String name = libraryObject.getString("library");
                int visitCount = libraryObject.getInt("visit_count");

                View libraryView = getLayoutInflater().inflate(R.layout.item_recent_library, recentLibrariesContainer, false);

                ImageView libraryImage = libraryView.findViewById(R.id.library_image);
                TextView libraryName = libraryView.findViewById(R.id.library_name);
                TextView visitCountView = libraryView.findViewById(R.id.visit_count);

                // TODO: Set the correct image for each library
                libraryImage.setImageResource(R.drawable.img_cn_library);
                libraryName.setText(name);
                visitCountView.setText("방문횟수: " + visitCount);

                recentLibrariesContainer.addView(libraryView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadRecommendedBooks() {
        try {
            String jsonString = loadJSONFromRaw(R.raw.recommendation_book);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("books");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bookObject = jsonArray.getJSONObject(i);
                String bookname = bookObject.getString("bookname");
                String authors = bookObject.getString("authors");
                String bookimageURL = bookObject.getString("bookimageURL");

                View bookView = getLayoutInflater().inflate(R.layout.item_recommended_book, recommendedBooksContainer, false);

                ImageView bookImage = bookView.findViewById(R.id.book_image);
                TextView bookNameView = bookView.findViewById(R.id.book_name);
                TextView authorView = bookView.findViewById(R.id.book_author);

                Glide.with(this).load(bookimageURL).into(bookImage);
                bookNameView.setText(bookname);
                authorView.setText("▸ 저자: " + authors);

                recommendedBooksContainer.addView(bookView);

                if (i < jsonArray.length() - 1) {
                    View divider = new View(getContext());
                    divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    divider.setBackgroundColor(getResources().getColor(R.color.gray));
                    recommendedBooksContainer.addView(divider);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }

    private void performSearch(String query) {
        // TODO: Implement search functionality
    }
}