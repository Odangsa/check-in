//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import com.kakao.sdk.auth.LoginClient;
//import com.kakao.sdk.auth.model.OAuthToken;
//import com.kakao.sdk.user.UserApiClient;
//
//public class LoginActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        Button kakaoLoginButton = findViewById(R.id.kakao_login_button);
//        kakaoLoginButton.setOnClickListener(v -> kakaoLogin());
//    }
//
//    private void kakaoLogin() {
//        if(LoginClient.getInstance().isKakaoTalkLoginAvailable(this)){
//            LoginClient.getInstance().loginWithKakaoTalk(this, (oAuthToken, error) -> {
//                if (error != null) {
//                    Toast.makeText(this, "카카오톡 로그인 실패", Toast.LENGTH_SHORT).show();
//                } else if (oAuthToken != null) {
//                    Toast.makeText(this, "카카오톡 로그인 성공", Toast.LENGTH_SHORT).show();
//                    startMainActivity();
//                }
//                return null;
//            });
//        } else {
//            LoginClient.getInstance().loginWithKakaoAccount(this, (oAuthToken, error) -> {
//                if (error != null) {
//                    Toast.makeText(this, "카카오 계정 로그인 실패", Toast.LENGTH_SHORT).show();
//                } else if (oAuthToken != null) {
//                    Toast.makeText(this, "카카오 계정 로그인 성공", Toast.LENGTH_SHORT).show();
//                    startMainActivity();
//                }
//                return null;
//            });
//        }
//    }
//
//    private void startMainActivity() {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
//}