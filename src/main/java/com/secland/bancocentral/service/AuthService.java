package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.RegisterUserDto;
import com.secland.bancocentral.model.User;

public interface AuthService {
    User register(RegisterUserDto registerUserDto);
}