package com.efforttracker.model.mapper;

import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;


public class UserMapper {

    public static UserDtos.UserResponse toUserResponse(User user) {
        if (user == null) return null;
        return new UserDtos.UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            user.getHourlyRate(),
            user.getNotes()
        );
    }
}
