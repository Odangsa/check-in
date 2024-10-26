package com.example.check.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.check.MainActivity;
import com.example.check.R;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.model.login.LoginRequest;
import com.kakao.sdk.user.UserApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ApiClient를 사용하여 ApiService 초기화
        apiService = ApiClient.getClient().create(ApiService.class);

        ImageButton kakaoLoginButton = findViewById(R.id.kakao_login_button);
        kakaoLoginButton.setOnClickListener(v -> kakaoLogin());

        // 이미 로그인되어 있는지 확인
        UserApiClient.getInstance().accessTokenInfo((tokenInfo, error) -> {
            if (error == null && tokenInfo != null) {
                Log.d(TAG, "d" + tokenInfo);
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
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else {
                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: " + user.getId() +
                        "\n닉네임: " + user);

                try {
                    Long userid = user.getId();
                    String nickname = user.getKakaoAccount().getProfile().getNickname();
                    LoginRequest request = new LoginRequest(userid, nickname);

                    Log.d(TAG, "Request: " + request.toString());

                    if (apiService != null) {
                        Call<ResponseBody> call = apiService.postLogin(request);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "로그인 결과 전송 성공: " + request);
                                    // 여기서 MainActivity 실행
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("userId", String.valueOf(userid));
                                    Log.d(TAG, "전달하는 userId: " + String.valueOf(userid));
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.e(TAG, "로그인 결과 전송 실패: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e(TAG, "로그인 결과 전송 중 오류 발생: " + t.getMessage());
                            }
                        });
                    } else {
                        Log.e(TAG, "ApiService가 초기화되지 않았습니다");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "사용자 데이터 처리 중 오류 발생", e);
                }
            }
            return null;
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}