package com.example.check.model.home.recentLib;

public class RecommendedBook {
    private String ISBN;
    private String bookname;
    private String authors;
    private String topic;
    private String bookimageURL;



    public RecommendedBook(String ISBN, String bookname, String authors, String topic, String bookimageURL) {
        this.ISBN = ISBN;
        this.bookname = bookname;
        this.authors = authors;
        this.topic = topic;
        this.bookimageURL = bookimageURL;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getBookname() {
        return bookname;
    }

    public String getAuthors() {
        return authors;
    }

    public String getTopic() {
        return topic;
    }

    public String getBookimageURL() {
        return bookimageURL;
    }

    @Override
    public String toString() {
        return "RecommendedBook{" +
                "ISBN='" + ISBN + '\'' +
                ", bookname='" + bookname + '\'' +
                ", authors='" + authors + '\'' +
                ", topic='" + topic + '\'' +
                ", bookimageURL='" + bookimageURL + '\'' +
                '}';
    }

}

