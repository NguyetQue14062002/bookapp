package com.example.bookapp.Domain;

import java.io.Serializable;

public class Like implements Serializable {

    //id, post_id, user_id, status_id
    private int id, post_id, user_id, status_id;

    public Like(int post_id, int user_id, int status_id) {
        this.post_id = post_id;
        this.user_id = user_id;
        this.status_id = status_id;
    }

    public Like(int id, int post_id, int user_id, int status_id) {
        this.id = id;
        this.post_id = post_id;
        this.user_id = user_id;
        this.status_id = status_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }
}

