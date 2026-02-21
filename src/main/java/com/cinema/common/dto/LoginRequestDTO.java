package com.cinema.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequestDTO {
    public String username;
    public String password;

    public LoginRequestDTO() {}
}