// RecommendationCategory.java
package com.example.check.model.today_book;

import java.util.List;

public class RecommendationCategory {
    private String recommendationTitle;
    private List<Book> books;

    // Getter and setter methods

    public String getRecommendationTitle() {
        return recommendationTitle;
    }

    public void setRecommendationTitle(String recommendationTitle) {
        this.recommendationTitle = recommendationTitle;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}