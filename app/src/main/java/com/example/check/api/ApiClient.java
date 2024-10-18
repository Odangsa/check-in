package com.example.check.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Android 에뮬레이터에서 로컬 서버에 접근하기 위한 URL
    public static final String SERVER_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;
    private static final int TIMEOUT_SECONDS = 30;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
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

    public static GsonConverterFactory getConverterFactory() {
        return GsonConverterFactory.create();
    }
}