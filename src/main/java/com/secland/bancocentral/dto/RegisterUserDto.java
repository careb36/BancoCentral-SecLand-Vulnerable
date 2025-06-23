package com.secland.bancocentral.dto;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String username;
    private String password;
    private String fullName;
}