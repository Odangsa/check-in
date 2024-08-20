package com.example.check.model.bookDetail;

import java.util.List;

public class BookDetail {
    String ISBN;
    String bookname;
    String authors;
    String publisher;
    String publishyear;
    String bookimageURL;
    String description;
    List<Library> libs;

    public BookDetail(String ISBN, String bookname, String authors, String publisher, String publishyear, String bookimageURL, String description, List<Library> libs) {
        this.ISBN = ISBN;
        this.bookname = bookname;
        this.authors = authors;
        this.publisher = publisher;
        this.publishyear = publishyear;
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

    public String getPublisher() {
        return publisher;
    }

    public String getPublishyear() {
        return publishyear;
    }

    public String getBookimageURL() {
        return bookimageURL;
    }

    public String getDescription() {
        return description;
    }

    public List<Library> getLibs() {
        return libs;
    }

    // getters and setters

}
