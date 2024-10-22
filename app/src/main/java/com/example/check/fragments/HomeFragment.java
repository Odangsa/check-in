package com.example.check.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.check.model.home.RecentLibrariesWrapper;
import com.example.check.model.home.RecentLibrary;
import com.example.check.model.home.RecommendedBook;
import com.example.check.model.home.RecommendedBooksWrapper;
import com.example.check.fragments.todayBook.BookDetailFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private LinearLayout recentLibrariesContainer;
    private LinearLayout recommendedBooksContainer;
    private ApiService apiService;
    private ImageView bbtiImage;
    private TextView bbtiTitle;
    private Handler handler;
    private static final int BBTI_CHECK_INTERVAL = 1000; // 1초마다 확인
    private static final int MAX_BBTI_CHECK_ATTEMPTS = 10; // 최대 10번 시도
    private int bbtiCheckAttempts = 0;
    private JSONArray bbtiResults;



    private final Runnable bbtiCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (MainActivity.bbtiNumber != null) {
                updateBBTIView();
            } else {
                handler.postDelayed(this, BBTI_CHECK_INTERVAL);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recentLibrariesContainer = view.findViewById(R.id.recent_libraries_container);
        recommendedBooksContainer = view.findViewById(R.id.recommended_books_container);
        bbtiImage = view.findViewById(R.id.bbti_image);
        bbtiTitle = view.findViewById(R.id.bbti_title);

        apiService = ApiClient.getClient().create(ApiService.class);
        handler = new Handler(Looper.getMainLooper());

        loadRecentLibraries();
        loadRecommendedBooks();
        startBBTICheck();
        loadBBTIResults();

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

        handler = new Handler(Looper.getMainLooper());


        return view;
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



    @Override
    public void onResume() {
        super.onResume();
        handler.post(bbtiCheckRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(bbtiCheckRunnable);
    }


    private void startBBTICheck() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.bbtiNumber != null) {
                    updateBBTIView();
                } else if (bbtiCheckAttempts < MAX_BBTI_CHECK_ATTEMPTS) {
                    bbtiCheckAttempts++;
                    handler.postDelayed(this, BBTI_CHECK_INTERVAL);
                } else {
                    Log.e(TAG, "Failed to load BBTI after maximum attempts");
                    showToast("BBTI 정보를 불러오는데 실패했습니다.");
                }
            }
        }, BBTI_CHECK_INTERVAL);
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
        if (bbtiResults == null || bbtiResults.length() == 0) {
            Log.e(TAG, "BBTI results not loaded");
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
        ImageView bbtiImage = getView().findViewById(R.id.bbti_image);
        TextView bbtiTitle = getView().findViewById(R.id.bbti_title);

        String imageName = bbtiInfo.getString("이미지").replace(".png", "");
        int imageResId = getResources().getIdentifier(imageName, "drawable", requireContext().getPackageName());
        if (imageResId != 0) {
            bbtiImage.setImageResource(imageResId);
        } else {
            Log.e(TAG, "Image resource not found: " + imageName);
            bbtiImage.setImageResource(R.drawable.img_bookbti_tmp);
        }

        bbtiTitle.setText(bbtiInfo.getString("타이틀"));
    }



    private void loadRecentLibraries() {
        apiService.getRecentLibraries(MainActivity.userId).enqueue(new Callback<RecentLibrariesWrapper>() {
            @Override
            public void onResponse(Call<RecentLibrariesWrapper> call, Response<RecentLibrariesWrapper> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RecentLibrary> libraries = response.body().getRecentLibraries();
                    if (libraries != null && !libraries.isEmpty()) {
                        recentLibrariesContainer.removeAllViews();
                        for (RecentLibrary library : libraries) {
                            addLibraryView(library.getLibrary(), library.getVisitCount());
                        }
                        Log.d(TAG, "Recent libraries loaded successfully");
                    } else {
                        Log.e(TAG, "No recent libraries found");
                        showToast("최근 도서관 데이터를 찾을 수 없습니다.");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch recent libraries: " + response.code());
                    showToast("최근 도서관 데이터를 가져오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RecentLibrariesWrapper> call, Throwable t) {
                Log.e(TAG, "Error fetching recent libraries", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    private void addBookView(RecommendedBook book) {
        // Fragment가 attached 되어있는지 확인
        if (!isAdded() || getActivity() == null) {
            Log.e(TAG, "Fragment is not attached or activity is null");
            return;
        }

        // 메인 스레드에서 UI 업데이트
        getActivity().runOnUiThread(() -> {
            try {
                // Fragment가 여전히 attached 되어있는지 다시 확인
                if (!isAdded() || getActivity() == null) {
                    return;
                }

                // Activity의 LayoutInflater 사용
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View bookView = inflater.inflate(R.layout.item_recommended_book, recommendedBooksContainer, false);

                // UI 컴포넌트 설정
                ImageView bookImage = bookView.findViewById(R.id.book_image);
                TextView bookNameView = bookView.findViewById(R.id.book_name);
                TextView authorView = bookView.findViewById(R.id.book_author);
                TextView detailView = bookView.findViewById(R.id.btn_book_detail);

                // UI 업데이트
                if (isAdded()) {  // 한번 더 확인
                    Glide.with(requireContext()).load(book.getBookimageURL()).into(bookImage);
                    bookNameView.setText(book.getBookname());
                    authorView.setText("▸ 저자: " + book.getAuthors());

                    detailView.setOnClickListener(v -> {
                        if (isAdded()) {  // 클릭 시에도 확인
                            BookDetailFragment detailFragment = BookDetailFragment.newInstance(book.getISBN());
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, detailFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                    recommendedBooksContainer.addView(bookView);

                    // 구분선 추가
                    if (recommendedBooksContainer.getChildCount() > 1) {
                        View divider = new View(requireContext());
                        divider.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 1));
                        divider.setBackgroundColor(getResources().getColor(R.color.gray));
                        recommendedBooksContainer.addView(divider);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding book view", e);
            }
        });
    }

    private void loadRecommendedBooks() {
        if (!isAdded()) return;

        apiService.getRecommendedBooks().enqueue(new Callback<RecommendedBooksWrapper>() {
            @Override
            public void onResponse(Call<RecommendedBooksWrapper> call, Response<RecommendedBooksWrapper> response) {
                if (!isAdded() || getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    try {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            List<RecommendedBook> books = response.body().getBooks();
                            if (books != null && !books.isEmpty()) {
                                recommendedBooksContainer.removeAllViews();
                                for (RecommendedBook book : books) {
                                    addBookView(book);
                                }
                                Log.d(TAG, "Recommended books loaded successfully");
                            } else {
                                Log.e(TAG, "No recommended books found");
                                showToast("추천 도서 데이터를 찾을 수 없습니다.");
                            }
                        } else {
                            Log.e(TAG, "Failed to fetch recommended books: " + response.code());
                            showToast("추천 도서 데이터를 가져오는데 실패했습니다.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing recommended books response", e);
                    }
                });
            }

            @Override
            public void onFailure(Call<RecommendedBooksWrapper> call, Throwable t) {
                if (!isAdded() || getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    Log.e(TAG, "Error fetching recommended books", t);
                    showToast("네트워크 오류가 발생했습니다.");
                });
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



    private void performSearch(String query) {
        // TODO: Implement search functionality
        Log.d(TAG, "Performing search with query: " + query);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Handler 콜백 제거
        handler.removeCallbacksAndMessages(null);
        // View 참조 정리
        bbtiImage = null;
        bbtiTitle = null;
    }
}