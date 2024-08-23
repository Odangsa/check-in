package com.example.check;

import android.os.Bundle;
import android.util.Log;

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
import com.example.check.fragments.StampMap.StampFragment;
import com.example.check.fragments.TodayBookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.vectormap.KakaoMapSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    public static final String userId = "123456789";
    private static final String TAG = "MainActivity";
    private ApiService apiService;
    private LocationUtil locationUtil;

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
        // API 서비스 초기화
        apiService = ApiClient.getClient().create(ApiService.class);

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
            } else if (itemId == R.id.navigation_person) {
                selectedFragment = new StampFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
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