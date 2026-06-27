package com.shundev.identity_service.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shundev.enums.Role;
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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServices {

    IUserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {

        log.info("Service: Create User");
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }
        
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        //set default role to USER
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);
        
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
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}