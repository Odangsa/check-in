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
import com.example.check.Utils.BBTIUtil;
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

import java.io.IOException;
import java.io.InputStream;
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



    private void updateBBTIView() {
        try {
            String jsonString = loadJSONFromAsset("bbti.json");
            JSONObject bbtiData = new JSONObject(jsonString);
            BBTIUtil.BBTINumberResult result = BBTIUtil.getBBTIResultByNumber(bbtiData, MainActivity.bbtiNumber);

            if (result != null) {
                Glide.with(this)
                        .load(getResources().getIdentifier(result.imageName, "drawable", requireContext().getPackageName()))
                        .into(bbtiImage);
                bbtiTitle.setText(result.title);
            } else {
                Log.e(TAG, "BBTI result not found for number: " + MainActivity.bbtiNumber);
                showToast("BBTI 결과를 찾을 수 없습니다.");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing BBTI JSON: " + e.getMessage());
            showToast("BBTI 데이터 파싱 중 오류가 발생했습니다.");
        } catch (IOException e) {
            Log.e(TAG, "Error reading BBTI JSON file: " + e.getMessage());
            showToast("BBTI 데이터 파일을 읽는 중 오류가 발생했습니다.");
        }
    }


    private String loadJSONFromAsset(String fileName) throws IOException {
        String json;
        try {
            InputStream is = getContext().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return json;
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

    private void loadRecommendedBooks() {
        apiService.getRecommendedBooks().enqueue(new Callback<RecommendedBooksWrapper>() {
            @Override
            public void onResponse(Call<RecommendedBooksWrapper> call, Response<RecommendedBooksWrapper> response) {
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

    private void addBookView(RecommendedBook book) {
        View bookView = getLayoutInflater().inflate(R.layout.item_recommended_book, recommendedBooksContainer, false);

        ImageView bookImage = bookView.findViewById(R.id.book_image);
        TextView bookNameView = bookView.findViewById(R.id.book_name);
        TextView authorView = bookView.findViewById(R.id.book_author);
        TextView detailView = bookView.findViewById(R.id.btn_book_detail);

        Glide.with(this).load(book.getBookimageURL()).into(bookImage);
        bookNameView.setText(book.getBookname());
        authorView.setText("▸ 저자: " + book.getAuthors());

        detailView.setOnClickListener(v -> {
            BookDetailFragment detailFragment = BookDetailFragment.newInstance(book.getISBN());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}