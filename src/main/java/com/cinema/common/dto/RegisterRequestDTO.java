package com.cinema.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequestDTO {
    public String username;
    public String password;
    public String email;
    public String phone;

    public RegisterRequestDTO() {}
}