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

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishyear(String publishyear) {
        this.publishyear = publishyear;
    }

    public void setBookimageURL(String bookimageURL) {
        this.bookimageURL = bookimageURL;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLibs(List<Library> libs) {
        this.libs = libs;
    }

    // getters and setters

}
