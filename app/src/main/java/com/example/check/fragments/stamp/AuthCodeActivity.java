package com.example.check.fragments.stamp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.check.MainActivity;
import com.example.check.R;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.model.stampboard.StampRegisterRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthCodeActivity extends AppCompatActivity {

    private StringBuilder codeBuilder = new StringBuilder();
    private TextView[] codeViews = new TextView[4];
    private ApiService apiService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_code_layout);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Back 버튼 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // 코드 표시 TextView 초기화
        codeViews[0] = findViewById(R.id.code1);
        codeViews[1] = findViewById(R.id.code2);
        codeViews[2] = findViewById(R.id.code3);
        codeViews[3] = findViewById(R.id.code4);

        // 숫자 버튼 설정
        setupNumberButtons();
    }

    private void setupNumberButtons() {
        int[] buttonIds = {
                R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4,
                R.id.key5, R.id.key6, R.id.key7, R.id.key8, R.id.key9
        };

        for (int id : buttonIds) {
            TextView button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(v -> {
                    String number = ((TextView) v).getText().toString();
                    onNumberClick(number);
                });
            }
        }
    }

    private void onNumberClick(String number) {
        if (codeBuilder.length() < 4) {
            codeBuilder.append(number);
            updateCodeDisplay();

            // 4자리가 모두 입력되면 자동으로 검증 시작
            if (codeBuilder.length() == 4) {
                verifyCode(codeBuilder.toString());
            }
        }
    }

    private void updateCodeDisplay() {
        String code = codeBuilder.toString();
        for (int i = 0; i < 4; i++) {
            if (i < code.length()) {
                codeViews[i].setText(String.valueOf(code.charAt(i)));
            } else {
                codeViews[i].setText("-");
            }
        }
    }

    private void verifyCode(String code) {
        // 현재 날짜를 "yyMMdd" 형식으로 포맷팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // API 요청 객체 생성
        StampRegisterRequest request = new StampRegisterRequest(
                String.valueOf(MainActivity.userId),
                code,
                currentDate
        );

        // API 호출
        apiService.registerStamp(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AuthCodeActivity.this, "스탬프가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AuthCodeActivity.this, "잘못된 인증 코드입니다.", Toast.LENGTH_SHORT).show();
                    resetCode();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AuthCodeActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                resetCode();
            }
        });
    }
    private void resetCode() {
        codeBuilder.setLength(0);
        updateCodeDisplay();
    }

}
