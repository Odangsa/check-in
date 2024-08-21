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
import com.example.check.model.today_book.Book;
import com.example.check.model.today_book.RecommendationCategory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TodayBookFragment extends Fragment implements RecommendationAdapter.OnBookClickListener {
    private static final String TAG = "TodayBookFragment";
    private RecyclerView recyclerView;
    private RecommendationAdapter adapter;

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

        List<RecommendationCategory> categories = loadRecommendations();
        adapter = new RecommendationAdapter(categories, this);
        recyclerView.setAdapter(adapter);
    }

    private List<RecommendationCategory> loadRecommendations() {
        List<RecommendationCategory> categories = new ArrayList<>();
        try {
            InputStream is = getResources().openRawResource(R.raw.recommendations_today_book);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            is.close();

            JSONArray jsonArray = new JSONArray(jsonString.toString());
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONArray recommendationsArray = jsonObject.getJSONArray("recommendations");

            Gson gson = new Gson();
            Type categoryListType = new TypeToken<List<RecommendationCategory>>(){}.getType();
            categories = gson.fromJson(recommendationsArray.toString(), categoryListType);

        } catch (Exception e) {
            Log.e(TAG, "Error loading recommendations", e);
        }
        return categories;
    }

    @Override
    public void onBookClick(Book book) {
        Log.d(TAG, "Book clicked: " + book.getISBN());
        BookDetailFragment fragment = BookDetailFragment.newInstance(book.getISBN());
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}