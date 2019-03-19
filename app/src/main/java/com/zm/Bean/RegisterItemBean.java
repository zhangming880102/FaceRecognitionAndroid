package com.zm.Bean;

public class RegisterItemBean {
    private String user;
    private String name;
    private String review_image_path;
    private int review_status;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview_image_path() {
        return review_image_path;
    }

    public void setReview_image_path(String review_image_path) {
        this.review_image_path = review_image_path;
    }

    public int getReview_status() {
        return review_status;
    }

    public void setReview_status(int review_status) {
        this.review_status = review_status;
    }
}
