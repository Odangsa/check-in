package com.example.check;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.check.Utils.LocationUtil;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.fragments.BBTIFragment;
import com.example.check.fragments.HomeFragment;
//import com.example.check.fragments.StampFragment;
import com.example.check.fragments.StampBoardFragment;
import com.example.check.fragments.TodayBookFragment;
import com.example.check.map.LibraryMapFragment;
import com.example.check.model.bbti.BBTIResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.vectormap.KakaoMapSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    public static final String userId = "2";
    private static final String TAG = "MainActivity";
    private ApiService apiService;
    private LocationUtil locationUtil;
    public static String bbtiNumber;


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

        KakaoMapSdk.init(this, "768f4a0e53607a3a3be9e0348e0a3ee2");
        apiService = ApiClient.getClient().create(ApiService.class);

        loadBBTIResult();
        setupBottomNavigation();

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
            } else if (itemId == R.id.navigation_person) {
                selectedFragment = new StampBoardFragment();
//                selectedFragment = new LibraryMapFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }



    private void loadBBTIResult() {
        apiService.getBBTI(Integer.parseInt(userId)).enqueue(new Callback<BBTIResponse>() {
            @Override
            public void onResponse(Call<BBTIResponse> call, Response<BBTIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BBTIResponse result = response.body();
                    if (result.isSuccess()) {
                        bbtiNumber = result.getBbti();
                        Log.d(TAG, "BBTI number loaded: " + bbtiNumber);
                    } else {
                        Log.e(TAG, "Failed to get BBTI result");
                        showToast("BBTI 결과를 가져오는데 실패했습니다.");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch BBTI result: " + response.code());
                    showToast("BBTI 결과를 가져오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<BBTIResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching BBTI result", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


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