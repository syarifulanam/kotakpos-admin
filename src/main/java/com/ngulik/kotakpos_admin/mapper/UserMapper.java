package com.ngulik.kotakpos_admin.mapper;

import com.ngulik.kotakpos_admin.dto.UserDto;
import com.ngulik.kotakpos_admin.dto.UserProfileDto;
import com.ngulik.kotakpos_admin.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus());
        return user;
    }

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        // Password is usually not sent back in DTO for security reasons, or handled specifically
        return dto;
    }

    public void updateEntityFromDto(UserDto dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus());
        // Password update logic is usually handled separately in service
    }

    public UserProfileDto toUserProfileDto(User user) {
        if (user == null) {
            return null;
        }

        UserProfileDto dto = new UserProfileDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setJoinedSince(user.getCreatedAt());
        return dto;
    }
}
