package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.dto.UserDto;
import com.ngulik.kotakpos_admin.entity.User;
import com.ngulik.kotakpos_admin.enums.UserRole;
import com.ngulik.kotakpos_admin.enums.UserStatus;
import com.ngulik.kotakpos_admin.mapper.UserMapper;
import com.ngulik.kotakpos_admin.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(UserRole.CASHIER); // default role for registration
        user.setStatus(UserStatus.INACTIVE); // default status
        return userRepository.save(user);
    }

    public Page<UserDto> getAllUsers(String name, String email, UserRole role, UserStatus status, Pageable pageable) {
        return userRepository.search(name, email, role, status, pageable).map(userMapper::toDto);
    }
}