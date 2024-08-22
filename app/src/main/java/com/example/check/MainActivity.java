package com.example.check;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.fragments.BBTIFragment;
import com.example.check.fragments.HomeFragment;
import com.example.check.fragments.TodayBookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    public static final String userId = "123456789";
    private static final String TAG = "MainActivity";
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // API 서비스 초기화
        apiService = ApiClient.getClient().create(ApiService.class);

        // API 호출
        fetchAllApiData();

        // BottomNavigationView 설정 (기존 코드 유지)
        setupBottomNavigation();

        // 초기 Fragment 설정
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    // BottomNavigationView 설정 (기존 코드 유지)
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_bbti) {
                selectedFragment = new BBTIFragment();
            } else if (itemId == R.id.navigation_today_book) {
                selectedFragment = new TodayBookFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }

    private void fetchAllApiData() {
//        fetchTodayBooks();
//        fetchRecommendedBooks();
//        fetchRecentLibraries();
//        fetchBookDetails("1231231241214");
        fetchStampBoard();
    }

//    private void fetchBookDetails(String isbn) {
//        apiService.getBookDetails(isbn).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        String jsonResponse = response.body().string();
//                        Log.d(TAG, "Book Details Raw Response:\n" + formatJson(jsonResponse));
//                        // 여기에서 JSON 응답을 파싱하고 UI를 업데이트하는 로직을 추가할 수 있습니다.
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error reading Book Details response", e);
//                    }
//                } else {
//                    Log.e(TAG, "Failed to fetch Book Details: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "Error fetching Book Details", t);
//            }
//        });
//    }

//    private void fetchTodayBooks() {
//        apiService.getTodayBooks().enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        String jsonResponse = response.body().string();
//                        Log.d(TAG, "Today Books Raw Response:\n" + formatJson(jsonResponse));
//                        // JSON 파싱 및 처리 로직은 여기에 추가
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error reading Today Books response", e);
//                    }
//                } else {
//                    Log.e(TAG, "Failed to fetch Today Books: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "Error fetching Today Books", t);
//            }
//        });
//    }

//    private void fetchRecommendedBooks() {
//        apiService.getRecommendedBooks().enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        String jsonResponse = response.body().string();
//                        Log.d(TAG, "Recommended Books Raw Response:\n" + formatJson(jsonResponse));
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error reading Recommended Books response", e);
//                    }
//                } else {
//                    Log.e(TAG, "Failed to fetch Recommended Books: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "Error fetching Recommended Books", t);
//            }
//        });
//    }

//    private void fetchRecentLibraries() {
//        apiService.getRecentLibraries(userId).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        String jsonResponse = response.body().string();
//                        Log.d(TAG, "Recent Libraries Raw Response:\n" + formatJson(jsonResponse));
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error reading Recent Libraries response", e);
//                    }
//                } else {
//                    Log.e(TAG, "Failed to fetch Recent Libraries: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "Error fetching Recent Libraries", t);
//            }
//        });
//    }

    private void fetchStampBoard() {
        apiService.getStampBoard(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        Log.d(TAG, "Stamp Board Raw Response:\n" + formatJson(jsonResponse));
                        // 여기에서 jsonResponse를 파싱하고 UI를 업데이트하는 로직을 추가할 수 있습니다.
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading Stamp Board response", e);
                    }
                } else {
                    Log.e(TAG, "Failed to fetch Stamp Board: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error fetching Stamp Board", t);
            }
        });
    }

    // 새로운 메소드: 도장 인증
//    private void verifyStamp(String verificationCode, String date) {
//        apiService.verifyStamp(userId, verificationCode, date).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        String jsonResponse = response.body().string();
//                        Log.d(TAG, "Stamp Verification Response:\n" + formatJson(jsonResponse));
//                        // 여기에서 jsonResponse를 파싱하고 UI를 업데이트하는 로직을 추가할 수 있습니다.
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error reading Stamp Verification response", e);
//                    }
//                } else {
//                    Log.e(TAG, "Failed to verify stamp: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "Error verifying stamp", t);
//            }
//        });
//    }

    private String formatJson(String jsonStr) {
        try {
            if (jsonStr.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                return jsonObject.toString(4);
            } else if (jsonStr.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonStr);
                return jsonArray.toString(4);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error formatting JSON", e);
        }
        return jsonStr; // 포맷팅에 실패한 경우 원본 문자열 반환
    }
}