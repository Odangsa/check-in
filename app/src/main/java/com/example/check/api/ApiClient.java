package com.example.check.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String MOCK_URL = "http://10.0.2.2:8080/";
    private static final String REAL_SERVER_URL = "http://your-real-server-url.com/"; // 실제 서버 URL로 변경해야 함
    private static Retrofit retrofit = null;
    private static boolean useMockServer = true; // 모의 서버 사용 여부를 제어하는 플래그

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().build();

            String baseUrl = useMockServer ? MOCK_URL : REAL_SERVER_URL;

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void setUseMockServer(boolean use) {
        if (useMockServer != use) {
            useMockServer = use;
            retrofit = null; // Retrofit 인스턴스를 재설정하여 새 URL로 다시 생성되도록 함
        }
    }

    public static void resetClient() {
        retrofit = null;
    }
}