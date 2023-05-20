package com.example.bookapp.Domain;

import java.sql.Date;

public class Post {
    private int id, status_id;
    private String tcontent, image, user, avatar;
    private Date createAt;
    private Integer numLikes, numShares, numComments, bookId;
    private Boolean isLiked;
    public Post(int id, String user, int status_id, String tcontent, String image, Date createAt, Integer numLikes, Integer numShares, Integer numComments) {
        this.id = id;
        this.user = user;
        this.status_id = status_id;
        this.tcontent = tcontent;
        this.image = image;
        this.createAt = createAt;
        this.numLikes = numLikes;
        this.numShares = numShares;
        this.numComments = numComments;
    }

    public Post(int id, int status_id, String tcontent, String image, String user) {
        this.id = id;
        this.status_id = status_id;
        this.tcontent = tcontent;
        this.image = image;
        this.user = user;
    }

    public Post() {
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(Integer numLikes) {
        this.numLikes = numLikes;
    }

    public Integer getNumShares() {
        return numShares;
    }

    public void setNumShares(Integer numShares) {
        this.numShares = numShares;
    }

    public Integer getNumComments() {
        return numComments;
    }

    public void setNumComments(Integer numComments) {
        this.numComments = numComments;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
