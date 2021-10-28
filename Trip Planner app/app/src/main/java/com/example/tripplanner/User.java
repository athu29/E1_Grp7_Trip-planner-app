package com.example.tripplanner;

import android.net.Uri;

public class User {
    public String name;
    public String mail;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String imageURL;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String phone;
    int age;

    public User() {
    }

    public User(String name, String mail, String phone, int age, String imageURL) {
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.age = age;
        this.imageURL = imageURL;
    }
}
