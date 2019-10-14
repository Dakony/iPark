package com.example.android.ipark.models;

/**
 * Created by DAKONY on 9/26/2019.
 */

public class User {
    private String name;
    private String phone;
    private String plate_number;
    private String profile_image;
    private String user_id;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String longitude;
    private String latitude;
    private String postDescription;
    private String countPost;

    public User() {
    }



    public User(String name, String phone, String plate_number, String profile_image, String user_id, String saveCurrentDate, String saveCurrentTime, String longitude, String latitude, String postDescription, String countPost) {
        this.name = name;
        this.phone = phone;
        this.plate_number = plate_number;
        this.profile_image = profile_image;
        this.user_id = user_id;
        this.saveCurrentDate = saveCurrentDate;

        this.saveCurrentTime = saveCurrentTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.postDescription = postDescription;
        this.countPost = countPost;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getSaveCurrentDate() {
        return saveCurrentDate;
    }

    public void setSaveCurrentDate(String saveCurrentDate) {
        this.saveCurrentDate = saveCurrentDate;
    }

    public String getSaveCurrentTime() {
        return saveCurrentTime;
    }

    public void setSaveCurrentTime(String saveCurrentTime) {
        this.saveCurrentTime = saveCurrentTime;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getCountPost() {
        return countPost;
    }

    public void setCountPost(String countPost) {
        this.countPost = countPost;
    }
}
