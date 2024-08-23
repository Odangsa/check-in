package com.example.check.api;

import com.example.check.model.bookDetail.BookDetailModel;
import com.example.check.model.home.RecentLibrary;
import com.example.check.model.home.RecommendedBooksWrapper;
import com.example.check.model.stampboard.StampBoard;
import com.example.check.model.today_book.RecommendationsWrapper;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("/api/stamp_board")
    Call<StampBoard> getStampBoard(@Query("userId") int userId);


    @GET("api/book_details/{isbn}")
    Call<BookDetailModel> getBookDetails(
            @Path("isbn") String isbn,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude
    );

    @GET("api/recommendations_today_book")
    Call<RecommendationsWrapper> getTodayBooks(@Query("userId") String userId);


    // 홈화면 받아야할 데이터들
    // 최근 방문한 도서관
    @GET("api/recent_libraries/{userId}")
    Call<List<RecentLibrary>> getRecentLibraries(@Path("userId") String userId);


    //추천 도서 홈용
    @GET("api/recommendation_book_two")
    Call<RecommendedBooksWrapper> getRecommendedBooks();



    @GET("api/stamp_board/{userId}")
    Call<ResponseBody> getStampBoard(@Path("userId") String userId);

    @GET("api/stamp_verification")
    Call<ResponseBody> verifyStamp(
            @Query("userId") String userId,
            @Query("verificationCode") String verificationCode,
            @Query("date") String date
    );


}