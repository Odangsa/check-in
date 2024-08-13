package com.example.check.fragments;


import com.example.check.fragments.todaybook.BookDetailFragment;
import android.os.Bundle;
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
import com.example.check.model.today_book.Book;
import com.example.check.model.today_book.RecommendationCategory;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TodayBookFragment extends Fragment implements RecommendationAdapter.OnBookClickListener {
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
            InputStream is = getResources().openRawResource(R.raw.recommendations);
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
            for (int i = 0; i < recommendationsArray.length(); i++) {
                JSONObject categoryObject = recommendationsArray.getJSONObject(i);
                if (!categoryObject.has("recommendationTitle")) {
                    categoryObject.put("recommendationTitle", categoryObject.getString("type"));
                }
                RecommendationCategory category = gson.fromJson(categoryObject.toString(), RecommendationCategory.class);
                categories.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public void onBookClick(Book book) {

    }
}