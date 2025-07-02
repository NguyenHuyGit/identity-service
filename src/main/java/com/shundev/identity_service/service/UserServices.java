package com.shundev.identity_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shundev.identity_service.dto.request.UserCreationRequest;
import com.shundev.identity_service.dto.request.UserUpdateRequest;
import com.shundev.identity_service.entity.User;
import com.shundev.identity_service.repository.IUserRepository;

@Service
public class UserServices {

    @Autowired
    private IUserRepository userRepository;
    
    public UserServices() {
        // Constructor
    }
   
    public User createUser(UserCreationRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        return userRepository.save(user);
    }

    public User updateUser(String userId, UserUpdateRequest request){
        User userUpdate = getUser(userId);

        userUpdate.setPassword(request.getPassword());
        userUpdate.setFirstName(request.getFirstName());
        userUpdate.setLastName((request.getLastName()));
        userUpdate.setDob(request.getDob());
        return userRepository.save(userUpdate);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUser(String userId)
    {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId ));
    }

    public void deleteUser(String userId)
    {
        userRepository.deleteById(userId);
    }
}