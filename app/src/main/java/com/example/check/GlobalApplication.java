package com.example.check;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // KakaoSDK 초기화
        KakaoSdk.init(this, "768f4a0e53607a3a3be9e0348e0a3ee2"); // 기존의 네이티브 앱 키 사용
    }

    public static GlobalApplication getInstance() {
        return instance;
    }
}