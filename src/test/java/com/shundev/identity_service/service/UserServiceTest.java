package com.shundev.identity_service.service;

import java.lang.foreign.Linker.Option;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.shundev.identity_service.dto.request.UserCreationRequest;
import com.shundev.identity_service.dto.request.UserUpdateRequest;
import com.shundev.identity_service.dto.response.UserResponse;
import com.shundev.identity_service.entity.User;
import com.shundev.identity_service.exception.AppException;
import com.shundev.identity_service.mapper.UserMapper;
import com.shundev.identity_service.repository.IUserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when; 

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserServices userService; 

    @MockitoBean
    private IUserRepository userRepository;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private LocalDate dob;
    private User user;


    // hàm khởi tạo chung để chạy mỗi test case
    @BeforeEach
    void initData(){
        request = UserCreationRequest.builder()
        .username("john")
        .firstName("john")
        .lastName("doe")
        .password("1234567891")
        .build();

        userResponse = UserResponse.builder()
        .id("jfsjdfhsjdf")
        .username("john")
        .firstName("john")
        .lastName("doe")
        .dob(dob)
        .build();

        // define user existed in repository 
        user = User.builder()
            .id("jfsjdfhsjdf")
            .username("john")
            .firstName("john")
            .lastName("doe")
            .build();
    }
    
    @Test
    void createUser_valid_successful(){    
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        
        // When 
        var response = userService.createUser(request); // request is mock data define in init function
        
        // Then
        Assertions.assertThat(response.getId()).isEqualTo("jfsjdfhsjdf");
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
        
    }

    @Test
    void createUser_userExisted_fail(){
        // Given 
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When 
        var exception = assertThrows(AppException.class, 
                () -> userService.createUser(request));
        
        // Then
        Assertions.assertThat(exception.getErrorCode().getCode())
                    .isEqualTo(1002); 
    }

    @Test
    void updateUser_valid_successful(){
        // Given
        var userId = "jfsjdfhsjdf"; 
        var firstNameNew = "John Updated";
        var lastNameNew = "Doe Updated";
 
        UserUpdateRequest requestUpdate = UserUpdateRequest.builder()
                                            .firstName(firstNameNew)
                                            .lastName(lastNameNew)
                                            .build();

        User userSaved = User.builder()
                                .id(userId)
                                .username("john")
                                .firstName(firstNameNew)
                                .lastName(lastNameNew)
                                .build();

        // Mock the dependencies
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // third party 
        // when(userMapper.toUserResponse(any(User.class))).thenReturn(
        //     UserResponse.builder()
        //         .id(userId)
        //         .username("john")
        //         .firstName(firstNameNew)
        //         .lastName(lastNameNew)
        //         .build()
        // );
        when(userRepository.save(any(User.class))).thenReturn(userSaved);

        // When
        UserResponse response = userService.updateUser(userId, requestUpdate);
        
        // Then 
        Assertions.assertThat(response.getId()).isEqualTo(userId);
        Assertions.assertThat(response.getFirstName()).isEqualTo(firstNameNew);
        Assertions.assertThat(response.getLastName()).isEqualTo(lastNameNew);
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    void updateUser_userNotFound_throwException(){
        // Given
        var userId = "invalidUserId"; 
        
        UserUpdateRequest requestUpdate = UserUpdateRequest.builder()
                                            .firstName("New Name")
                                            .build();

        // Mock repository to return empty Optional (user not found)
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.updateUser(userId, requestUpdate))
                  .isInstanceOf(RuntimeException.class)
                  .hasMessage("User not found");
        
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    void updateUser_partialUpdate_successful(){
        // Given - Update only firstName, leave lastName unchanged
        var userId = "jfsjdfhsjdf";
        var newFirstName = "Jane";
        
        UserUpdateRequest requestUpdate = UserUpdateRequest.builder()
                                            .firstName(newFirstName)
                                            .build();

        User userToUpdate = User.builder()
                                .id(userId)
                                .username("john")
                                .firstName("john")
                                .lastName("doe")
                                .build();

        User userSaved = User.builder()
                                .id(userId)
                                .username("john")
                                .firstName(newFirstName)
                                .lastName("doe")
                                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.save(any(User.class))).thenReturn(userSaved);
        // when(userMapper.toUserResponse(any(User.class))).thenReturn(
        //     UserResponse.builder()
        //         .id(userId)
        //         .username("john")
        //         .firstName(newFirstName)
        //         .lastName("doe")
        //         .build()
        // );

        // When
        UserResponse response = userService.updateUser(userId, requestUpdate);

        // Then
        Assertions.assertThat(response.getFirstName()).isEqualTo(newFirstName);
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        // Mockito.verify(userMapper, Mockito.times(1)).updateUser(any(User.class), any(UserUpdateRequest.class));
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_valid_successful(){
        // void method not return 
        userService.deleteUser(user.getId());

        // Verify method delete call once time
        Mockito.verify(userRepository,Mockito.times((1))).deleteById(user.getId());
    }

    @Test 
    void getUser_valid_successful(){
        // given 
        var userId = "test";
        User userFound = User.builder()
                        .id(userId)
                        .firstName("testfname")
                        .lastName("testlname")
                        .build();

        UserResponse userResponse = UserResponse.builder()
                                .id(userId)
                                .firstName("testfname")
                                .lastName("testlname")
                                .build();

        //  when
        when(userRepository.findById(userId)).thenReturn(Optional.of(userFound));
        var response = userService.getUser(userId);

        // then 
        Assertions.assertThat(response.getFirstName()).isEqualTo(userResponse.getFirstName());
    }

    @Test
    void getUser_invalid_throwExceptions(){
        var userId = "invalidUserId"; 

        // when 
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // then
        Assertions.assertThatThrownBy(() -> userService.getUser(userId))
                  .isInstanceOf(RuntimeException.class)
                  .hasMessage("User not found");

        verify(userRepository,Mockito.times(1)).findById(userId);
    }
}
