package com.example.check.api;

import com.example.check.model.bookDetail.BookDetailModel;
import com.example.check.model.home.RecentLibrary;
import com.example.check.model.home.RecommendedBooksWrapper;
import com.example.check.model.today_book.RecommendationsWrapper;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // 상세화면 받아야할 데이터들 위치정보와 ISBN 정보를 넘겨야함
    @GET("api/book_details/{isbn}")
    Call<BookDetailModel> getBookDetails(@Path("isbn") String isbn);

    @GET("api/recommendations_today_book")
    Call<List<RecommendationsWrapper>> getTodayBooks();

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




//    @GET("api/book_details/{isbn}")
//    Call<ResponseBody> getBookDetails(@Path("isbn") String isbn);

//    @GET("api/recommendations_today_book")
//    Call<ResponseBody> getTodayBooks();

    // 썡 객체로 받기 - 이건 뷰에 띄우기 어렵다고 한다
//    @GET("api/recommendation_book_two")
//    Call<ResponseBody> getRecommendedBooks();

    //    @GET("api/recent_libraries/{userId}")
//    Call<ResponseBody> getRecentLibraries(@Path("userId") String userId);

}