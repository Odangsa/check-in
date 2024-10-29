package com.example.check.api;

import com.example.check.model.stampboard.Transportation;
import com.example.check.model.stampboard.TransportationDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
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

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Transportation.class, new TransportationDeserializer())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
    }

    public static GsonConverterFactory getConverterFactory() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Transportation.class, new TransportationDeserializer())
                .create();
        return GsonConverterFactory.create(gson);
    }
}