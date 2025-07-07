package com.shundev.identity_service.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shundev.identity_service.dto.request.UserCreationRequest;
import com.shundev.identity_service.dto.request.UserUpdateRequest;
import com.shundev.identity_service.dto.response.UserResponse;
import com.shundev.identity_service.entity.User;
import com.shundev.identity_service.exception.AppException;
import com.shundev.identity_service.exception.ErrorCode;
import com.shundev.identity_service.mapper.UserMapper;
import com.shundev.identity_service.repository.IUserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServices {

    IUserRepository userRepository;
    UserMapper userMapper;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }
        
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10); // strong of password: 1
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User userUpdate = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUser(userUpdate, request);

        return userMapper.toUserResponse(userRepository.save(userUpdate));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUser(String userId) {
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found ")));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}