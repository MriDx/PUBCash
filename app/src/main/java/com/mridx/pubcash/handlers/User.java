package com.mridx.pubcash.handlers;

public class User {

    private String fullname, username, email, gender, phone, dob, token, ingamename, ingameid;
    private int id;

    public User(int id, String fullname, String username, String email, String gender, String phone, String dob, String token, String ingamename) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.dob = dob;
        this.token = token;
        this.ingamename = ingamename;
    }

    public int getId() {
        return id;
    }
    public String getFullname() { return fullname; }
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }
    public String getPhone() { return phone; }
    public String getDob() { return dob; }
    public String getToken() {
        return token;
    }
    public String getIngamename() {
        return ingamename;
    }

}
