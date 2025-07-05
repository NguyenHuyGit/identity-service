package com.shundev.identity_service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shundev.identity_service.dto.request.ApiResponse;
import com.shundev.identity_service.dto.request.UserCreationRequest;
import com.shundev.identity_service.dto.request.UserUpdateRequest;
import com.shundev.identity_service.entity.User;
import com.shundev.identity_service.service.UserServices;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServices userServices;
    
    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) 
    {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userServices.createUser(request));
        return apiResponse;
    }
        

    @PutMapping("/{userId}")
    User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request)
    {
        return userServices.updateUser(userId,request);
    }
    
    @GetMapping
    List<User> getAllUsers() 
    {
        return userServices.getAllUsers();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable("userId") String userId)
    {
        return userServices.getUser(userId);
    }
    
    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable("userId") String userId)
    {
        userServices.deleteUser(userId);
        return "user has been deleted"; 
    }
}
