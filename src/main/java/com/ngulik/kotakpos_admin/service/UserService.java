package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.dto.ChangePasswordDto;
import com.ngulik.kotakpos_admin.dto.UserDto;
import com.ngulik.kotakpos_admin.dto.UserProfileDto;
import com.ngulik.kotakpos_admin.entity.User;
import com.ngulik.kotakpos_admin.enums.UserRole;
import com.ngulik.kotakpos_admin.enums.UserStatus;
import com.ngulik.kotakpos_admin.exception.error.BadRequestException;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.mapper.UserMapper;
import com.ngulik.kotakpos_admin.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Transactional
    public void saveUser(UserDto userDto) {
        User user;
        if (userDto.getId() == null) {
            // Create new user
            user = userMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        } else {
            //update existing user
            user = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            //Keep existing password
            userMapper.updateEntityFromDto(userDto, user);
            user.setPassword(user.getPassword());
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (StringUtils.hasText(newPassword)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    public UserProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfileDto dto = new UserProfileDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setJoinedSince(user.getCreatedAt());
        return dto;
    }

    @Transactional
    public UserProfileDto updateUserProfile(String currentEmail, UserProfileDto profileDto) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setName(profileDto.getName());
        user.setEmail(profileDto.getEmail());
        userRepository.save(user);

        return getUserProfile(currentEmail);
    }

    @Transactional
    public UserProfileDto changePassword(String email, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect current password");
        }

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        return getUserProfile(email);
    }
}