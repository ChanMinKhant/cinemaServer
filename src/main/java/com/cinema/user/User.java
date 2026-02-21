package com.cinema.user;

public class User {
    public Integer id;
    public String username;
    public String password;
    public String email;
    public String phone;
    public String role; // Maps to ENUM('user', 'admin')
}