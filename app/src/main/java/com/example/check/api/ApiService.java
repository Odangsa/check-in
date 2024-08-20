package com.example.check.api;

import com.example.check.model.bookDetail.BookDetail;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("lib")
    Call<String> getRecentLibraries();

    @GET("recommendation_book")
    Call<String> getRecommendedBooks();

    @GET("book_detail")
    Call<BookDetail> getBookDetail(@Query("isbn") String isbn);

    @GET("recommendations")
    Call<String> getRecommendations();
}