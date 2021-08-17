package com.eceakilli.instagramclonejava.model;

public class Post {

    public String email;
    public String comment;
    public String commentDetail;
    public String downloadUrl;

    public Post(String email, String comment, String commentDetail, String downloadUrl) {
        this.email = email;
        this.comment = comment;
        this.commentDetail=commentDetail;
        this.downloadUrl = downloadUrl;

    }
}
