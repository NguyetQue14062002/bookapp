package com.example.bookapp.Domain;

import java.io.Serializable;

public class User implements Serializable {
    private  int id, role_id;
    private String email;
    private String full_name;
    private String avatar;
    private String token;
    private String phone_number;
    private String password;

    private String access_token;
    private String refresh_token;

    public User(int id, int role_id, String email, String full_name, String avatar, String token, String phone_number, String access_token, String refresh_token) {
        this.id = id;
        this.role_id = role_id;
        this.email = email;
        this.full_name = full_name;
        this.avatar = avatar;
        this.token = token;
        this.phone_number = phone_number;
        this.access_token = access_token;
        this.refresh_token =refresh_token;

    }

    public User(String full_name) {
        this.full_name = full_name;
    }

    public User(int id, int role_id, String email, String full_name, String avatar, String token, String phone_number) {
        this.id = id;
        this.role_id = role_id;
        this.email = email;
        this.full_name = full_name;
        this.avatar = avatar;
        this.phone_number = phone_number;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
