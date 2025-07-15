package com.shundev.identity_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.shundev.identity_service.dto.request.UserCreationRequest;
import com.shundev.identity_service.dto.request.UserUpdateRequest;
import com.shundev.identity_service.dto.response.UserResponse;
import com.shundev.identity_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    UserResponse toUserResponse(User user);
}
