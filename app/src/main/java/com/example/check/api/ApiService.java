package com.example.check.api;

import com.example.check.model.bbti.BBTIResponse;
import com.example.check.model.bbti.BBTIResultRequest;
import com.example.check.model.bookDetail.BookDetailModel;
import com.example.check.model.home.RecentLibrariesWrapper;
import com.example.check.model.home.RecentLibrary;
import com.example.check.model.home.RecommendedBooksWrapper;
import com.example.check.model.stampboard.StampBoard;
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
    Call<BBTIResponse> getBBTI(@Query("userid") int userId);

    @GET("/api/stamp_board")
    Call<StampBoard> getStampBoard(@Query("userId") int userId);


    @GET("book/detail")
    Call<BookDetailModel> getBookDetails(
            @Query("isbn") String isbn,
            @Query("lng") double longitude,
            @Query("lat") double latitude
    );

    @GET("book/recommend/bbti")
    Call<RecommendationsWrapper> getTodayBooks(@Query("bbti") String bbtiNumber);



    // 홈화면 받아야할 데이터들
    // 최근 방문한 도서관
    @GET("/user/recentlib")
    Call<RecentLibrariesWrapper> getRecentLibraries(@Query("userid") String userId);

    //추천 도서 홈용
    @GET("/book/recommend/popular")
    Call<RecommendedBooksWrapper> getRecommendedBooks();



    @GET("api/stamp_board/{userId}")
    Call<ResponseBody> getStampBoard(@Path("userId") String userId);

    @GET("api/stamp_verification")
    Call<ResponseBody> verifyStamp(
            @Query("userId") String userId,
            @Query("verificationCode") String verificationCode,
            @Query("date") String date
    );

    @POST("user/bbti")
    Call<ResponseBody> postBBTIResult(@Body BBTIResultRequest request);


}