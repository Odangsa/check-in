package com.example.check.api;

import com.example.check.model.bbti.BBTIResponse;
import com.example.check.model.bbti.BBTIResultRequest;
import com.example.check.model.bookDetail.BookDetailModel;
import com.example.check.model.home.RecentLibrariesWrapper;
import com.example.check.model.home.RecommendedBooksWrapper;
import com.example.check.model.login.LoginRequest;
import com.example.check.model.stampboard.StampBoard;
import com.example.check.model.stampboard.StampRegisterRequest;
import com.example.check.model.today_book.RecommendationsWrapper;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("user/bbti")
    Call<BBTIResponse> getBBTI(@Query("userid") Long userId);

    @GET("/user/stamp/board")
    Call<StampBoard> getStampBoard(@Query("userid") Long userId);

    @GET("book/detail")
    Call<BookDetailModel> getBookDetails(
            @Query("isbn") String isbn,
            @Query("lng") double longitude,
            @Query("lat") double latitude
    );

    @GET("book/recommend/bbti")
    Call<RecommendationsWrapper> getTodayBooks(@Query("bbti") String bbtiNumber);

    @GET("/user/recentlib")
    Call<RecentLibrariesWrapper> getRecentLibraries(@Query("userid") Long userId);

    //추천 도서 홈용
    @GET("/book/recommend/popular")
    Call<RecommendedBooksWrapper> getRecommendedBooks();

    @POST("user/bbti")
    Call<ResponseBody> postBBTIResult(@Body BBTIResultRequest request);

//    /user/login
    @POST("/user/login")
    Call<ResponseBody> postLogin(@Body LoginRequest request);

    @POST("user/stamp/register")
    Call<ResponseBody> registerStamp(@Body StampRegisterRequest request);
}