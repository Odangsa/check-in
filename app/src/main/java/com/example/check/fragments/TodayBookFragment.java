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

import com.example.check.Adapter.today_book.RecommendationAdapter;
import com.example.check.MainActivity;
import com.example.check.R;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.fragments.todayBook.BookDetailFragment;
import com.example.check.model.today_book.Book;
import com.example.check.model.today_book.RecommendationCategory;
import com.example.check.model.today_book.RecommendationsWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TodayBookFragment extends Fragment implements RecommendationAdapter.OnBookClickListener {
    private static final String TAG = "TodayBookFragment";
    private RecyclerView recyclerView;
    private RecommendationAdapter adapter;
    private ApiService apiService;
    private ImageView bbtiImage;
    private TextView bbtiTitle;
    private TextView bbtiDescription;
    private JSONArray bbtiResults;

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

        bbtiImage = view.findViewById(R.id.bbtiImage);
        bbtiTitle = view.findViewById(R.id.bbtiTitle);
        bbtiDescription = view.findViewById(R.id.bbtiDescription);

        apiService = ApiClient.getClient().create(ApiService.class);
        loadBBTIResults();
        updateBBTIView();
        loadTodayBooks();
    }

    // ... (이전 코드와 동일)

    private void loadTodayBooks() {
        if (MainActivity.bbtiNumber != null) {
            Call<RecommendationsWrapper> call = apiService.getTodayBooks(MainActivity.bbtiNumber);
            call.enqueue(new Callback<RecommendationsWrapper>() {
                @Override
                public void onResponse(Call<RecommendationsWrapper> call, Response<RecommendationsWrapper> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RecommendationsWrapper recommendations = response.body();
                        updateRecommendations(recommendations.getRecommendations());
                        Log.d(TAG, "Successfully fetched today's books for BBTI: " + MainActivity.bbtiNumber);
                    } else {
                        Log.e(TAG, "Error fetching today's books: " + response.code());
                        showToast("오늘의 책을 불러오는데 실패했습니다. 다시 시도해주세요.");
                    }
                }

                @Override
                public void onFailure(Call<RecommendationsWrapper> call, Throwable t) {
                    Log.e(TAG, "Network error when fetching today's books: " + t.getMessage());
                    showToast("네트워크 오류가 발생했습니다. 연결을 확인하고 다시 시도해주세요.");
                    // Consider implementing a retry mechanism here
                }
            });
        } else {
            Log.e(TAG, "BBTI number is not available");
            showToast("BBTI 번호를 불러올 수 없습니다. 앱을 다시 시작해주세요.");
        }
    }

    private void setupApiService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.SERVER_URL)
                .client(client)
                .addConverterFactory(ApiClient.getConverterFactory())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void loadBBTIResults() {
        JSONObject bbtiResultsObj = loadJSONFromResource(R.raw.bbti);
        try {
            bbtiResults = bbtiResultsObj.getJSONArray("results");
            Log.d(TAG, "BBTI Results loaded: " + bbtiResults.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error loading BBTI results: " + e.getMessage());
            bbtiResults = new JSONArray();
        }
    }

    private JSONObject loadJSONFromResource(int resourceId) {
        try {
            InputStream is = getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading JSON from resource: " + e.getMessage());
            return new JSONObject();
        }
    }

    private void updateBBTIView() {
        if (bbtiResults == null || bbtiResults.length() == 0 || MainActivity.bbtiNumber == null) {
            Log.e(TAG, "BBTI results not loaded or BBTI number not available");
            return;
        }

        try {
            for (int i = 0; i < bbtiResults.length(); i++) {
                JSONObject result = bbtiResults.getJSONObject(i);
                if (result.getString("번호").equals(MainActivity.bbtiNumber)) {
                    updateUIWithBBTIInfo(result);
                    break;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error updating BBTI view: " + e.getMessage());
        }
    }

    private void updateUIWithBBTIInfo(JSONObject bbtiInfo) throws JSONException {
        String imageName = bbtiInfo.getString("이미지").replace(".png", "");
        int imageResId = getResources().getIdentifier(imageName, "drawable", requireContext().getPackageName());
        if (imageResId != 0) {
            bbtiImage.setImageResource(imageResId);
        } else {
            Log.e(TAG, "Image resource not found: " + imageName);
            bbtiImage.setImageResource(R.drawable.img_bookbti_tmp);
        }

        bbtiTitle.setText(bbtiInfo.getString("타이틀"));
        bbtiDescription.setText(bbtiInfo.getString("상세설명"));
    }



    private void updateRecommendations(List<RecommendationCategory> recommendations) {
        adapter = new RecommendationAdapter(recommendations, this);
        recyclerView.setAdapter(adapter);
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