package com.example.check.model.bookDetail;

import java.util.List;

public class BookDetail {
    private long ISBN;
    private String bookname;
    private String authors;
    private String publisher;
    private String publishyear;
    private String bookimageURL;
    private String description;
    private List<Library> libs;

    public long getISBN() {
        return ISBN;
    }

    public void setISBN(long ISBN) {
        this.ISBN = ISBN;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishyear() {
        return publishyear;
    }

    public void setPublishyear(String publishyear) {
        this.publishyear = publishyear;
    }

    public String getBookimageURL() {
        return bookimageURL;
    }

    public void setBookimageURL(String bookimageURL) {
        this.bookimageURL = bookimageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Library> getLibs() {
        return libs;
    }

    public void setLibs(List<Library> libs) {
        this.libs = libs;
    }
}