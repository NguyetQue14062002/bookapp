package com.example.bookapp.Domain;

public class Review {

    private int id, status_id, book_id, rate;
    private String tcontent, user;

    public Review() {
    }

    public Review(int id, int status_id, int book_id, String tcontent, String user, int rate) {
        this.id = id;
        this.status_id = status_id;
        this.book_id = book_id;
        this.tcontent = tcontent;
        this.user = user;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getTcontent() {
        return tcontent;
    }

    public void setTcontent(String tcontent) {
        this.tcontent = tcontent;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
