package com.example.check.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Android 에뮬레이터에서 로컬 서버에 접근하기 위한 URL
    private static final String SERVER_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)  // 로컬 서버에 연결
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Retrofit 인스턴스 초기화(필요 시 사용)
    public static void resetClient() {
        retrofit = null;
    }
}
