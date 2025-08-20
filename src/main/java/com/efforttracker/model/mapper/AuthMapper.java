package com.efforttracker.model.mapper;

import com.efforttracker.model.dto.AuthDtos;
import com.efforttracker.model.entity.User;

public class AuthMapper {

    public static AuthDtos.LoginResponse toLoginResponse(String token, long expiresIn, User user) {
        AuthDtos.LoginResponse dto = new AuthDtos.LoginResponse();
        dto.setAccessToken(token);
        dto.setTokenType("Bearer");
        dto.setExpiresIn(expiresIn);
        dto.setMe(UserMapper.toUserResponse(user));
        return dto;
    }
}
