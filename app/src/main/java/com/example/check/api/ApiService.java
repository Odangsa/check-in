package com.example.check.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/book_details/{isbn}")
    Call<ResponseBody> getBookDetails(@Path("isbn") String isbn);

    @GET("api/recommendations_today_book")
    Call<ResponseBody> getTodayBooks();

    @GET("api/recommendation_book_two")
    Call<ResponseBody> getRecommendedBooks();

    @GET("api/recent_libraries/{userId}")
    Call<ResponseBody> getRecentLibraries(@Path("userId") String userId);

    @GET("api/stamp_board/{userId}")
    Call<ResponseBody> getStampBoard(@Path("userId") String userId);

    @GET("api/stamp_verification")
    Call<ResponseBody> verifyStamp(
            @Query("userId") String userId,
            @Query("verificationCode") String verificationCode,
            @Query("date") String date
    );
}