package com.cinema.user;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    public Integer id;
    public String username;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String password; // Only allow setting password (input), never returning it (output)
    
    public String email;
    public String phone;
    public Integer balance;
    public String role;
    public Timestamp createdAt;

    public User() {}
}