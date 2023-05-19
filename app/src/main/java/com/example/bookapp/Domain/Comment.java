package com.example.bookapp.Domain;

import java.io.Serializable;

public class Comment implements Serializable {

    //id, post_id, user_id, tcontent, status_id
    private int id, post_id, user_id, status_id;
    private String tcontent, user_name;

    public Comment() {
    }

    public Comment(int post_id, int user_id, int status_id, String tcontent) {
        this.post_id = post_id;
        this.user_id = user_id;
        this.status_id = status_id;
        this.tcontent = tcontent;
    }

    public Comment(int id, int post_id, int user_id, int status_id, String tcontent) {
        this.id = id;
        this.post_id = post_id;
        this.user_id = user_id;
        this.status_id = status_id;
        this.tcontent = tcontent;
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

    public String getTcontent() {
        return tcontent;
    }

    public void setTcontent(String tcontent) {
        this.tcontent = tcontent;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
