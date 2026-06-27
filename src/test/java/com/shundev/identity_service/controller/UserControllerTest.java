package com.shundev.identity_service.controller;

import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shundev.identity_service.dto.request.UserCreationRequest;
import com.shundev.identity_service.dto.response.UserResponse;
import com.shundev.identity_service.service.UserServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // tạo mock request trong unit test 

    @MockitoBean // thay cho MockBean, mock 1 bean là 1 service
    private UserServices userService;
    
    private UserCreationRequest request;
    private UserResponse userResponse;
    private LocalDate dob;


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
    }

    @Test
    void createUser_validRequest_success() throws Exception{
        log.info("Hello test");
        // Given 
        // convert request as a string
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        // mock service create user if controller call this service
        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

        // When call api, and then Expect after call  
        mockMvc.perform(MockMvcRequestBuilders
            .post("/users")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)) // kết thúc build request 
            .andExpect(MockMvcResultMatchers.status().isOk()) // expect return về 1 API response 
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
            .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("jfsjdfhsjdf"))
        ;
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception{
        log.info("Hello test");
        // Given 

        request.setUsername("jb");
        // convert request as a string
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        // mock service create user if controller call this service
        // Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

        // When call api, and then Expect after call  
        mockMvc.perform(MockMvcRequestBuilders
            .post("/users")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)) // kết thúc build request 
            .andExpect(MockMvcResultMatchers.status().isBadRequest()) // expect return về 1 API response (400) 
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
            .andExpect(MockMvcResultMatchers.jsonPath("message").value("User is invalid"))
        ;
    }

    
}
