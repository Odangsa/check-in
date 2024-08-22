package com.example.check.model.today_book;

public class Book {
    private String ISBN;
    private String bookimageURL;

    public String getISBN() {
        return ISBN;
    }

    public String getBookimageURL() {
        String url = this.bookimageURL;
        if (url.startsWith("http://")) {
            url = "https://" + url.substring(7);
        }
        return url;
    }


    // Setters
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setBookimageURL(String bookimageURL) {
        this.bookimageURL = bookimageURL;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + ISBN + '\'' +
                ", bookimageURL='" + bookimageURL + '\'' +
                '}';
    }
}


