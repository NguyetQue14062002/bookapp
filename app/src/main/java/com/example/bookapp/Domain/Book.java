package com.example.bookapp.Domain;

import java.io.Serializable;

public class Book implements Serializable {
    private int id, category_id, status_id, publisher_id;
    private String author, description, image_url, link, title;

    public Book(String author, String image_url, String title) {
        this.author = author;
        this.image_url = image_url;
        this.title = title;
    }

    public Book(int id, int category_id, int status_id, String author, String description, String image_url, String link, String title) {
        this.id = id;
        this.category_id = category_id;
        this.status_id = status_id;
        this.author = author;
        this.description = description;
        this.image_url = image_url;
        this.link = link;
        this.title = title;
    }

    public Book(int id, int category_id, int status_id, int publisher_id, String author, String description, String image_url, String link, String title) {
        this.id = id;
        this.category_id = category_id;
        this.status_id = status_id;
        this.publisher_id = publisher_id;
        this.author = author;
        this.description = description;
        this.image_url = image_url;
        this.link = link;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public int getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(int publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
