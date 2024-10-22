package com.example.check.login;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.check.MainActivity;
import com.example.check.R;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton kakaoLoginButton = findViewById(R.id.kakao_login_button);
        kakaoLoginButton.setOnClickListener(v -> kakaoLogin());

        // 이미 로그인되어 있는지 확인
        UserApiClient.getInstance().accessTokenInfo((tokenInfo, error) -> {
            if (error == null && tokenInfo != null) {
                // 토큰 정보가 있으면 자동으로 메인 화면으로 이동
                moveToMainActivity();
            }
            return null;
        });
    }

    private void kakaoLogin() {
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            UserApiClient.getInstance().loginWithKakaoTalk(this, (oAuthToken, error) -> {
                if (error != null) {
                    Log.e(TAG, "카카오톡 로그인 실패", error);
                    // 카카오톡 로그인 실패 시 카카오계정으로 로그인 시도
                    kakaoAccountLogin();
                } else if (oAuthToken != null) {
                    Log.i(TAG, "카카오톡 로그인 성공");
                    moveToMainActivity();
                }
                return null;
            });
        } else {
            kakaoAccountLogin();
        }
    }

    private void kakaoAccountLogin() {
        UserApiClient.getInstance().loginWithKakaoAccount(this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오계정 로그인 실패", error);
                showToast("로그인에 실패했습니다.");
            } else if (oAuthToken != null) {
                Log.i(TAG, "카카오계정 로그인 성공");
                moveToMainActivity();
            }
            return null;
        });
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}