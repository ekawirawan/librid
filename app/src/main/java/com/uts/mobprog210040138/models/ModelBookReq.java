package com.uts.mobprog210040138.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ModelBookReq {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("publication_year")
    @Expose
    private String publicationYear;
    @SerializedName("isbn")
    @Expose
    private String isbn;
    @SerializedName("stock")
    @Expose
    private Integer stock;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("book_rack_location")
    @Expose
    private String bookRackLocation;

    public ModelBookReq(String titleP, String authorP, String publisherP, String publicationYearP, String isbnP, String imageUrlP, String bookRackLocationP, Integer stockP) {
        this.title = titleP;
        this.author = authorP;
        this.publisher = publisherP;
        this.publicationYear = publicationYearP;
        this.isbn = isbnP;
        this.stock = stockP;
        this.imageUrl = imageUrlP;
        this.bookRackLocation = bookRackLocationP;
    }

    public ModelBookReq(int parseInt, String authorP, String publisherP, String publicationYearP, String isbnP, String imageUrlP, String bookRackLocationP) {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBookRackLocation() {
        return bookRackLocation;
    }

    public void setBookRackLocation(String bookRackLocation) {
        this.bookRackLocation = bookRackLocation;
    }
}
