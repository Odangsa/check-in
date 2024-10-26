package com.example.check.model.today_book;

import java.util.List;

public class RecommendationCategory {
    private String type;
    private List<Book> books;

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "RecommendationCategory{" +
                "type='" + type + '\'' +
                ", books=" + books +
                '}';
    }
}