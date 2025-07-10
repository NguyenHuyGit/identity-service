package com.shundev.identity_service.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.shundev.identity_service.dto.request.ApiResponse;
import com.shundev.identity_service.dto.request.AuthenticationRequest;
import com.shundev.identity_service.dto.request.IntroSpectRequest;
import com.shundev.identity_service.dto.response.AuthenticationResponse;
import com.shundev.identity_service.dto.response.IntroSpectResponse;
import com.shundev.identity_service.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntroSpectResponse> authenticate(@RequestBody IntroSpectRequest request)
            throws JOSEException, ParseException {

        var result = authenticationService.introspect(request);
        return ApiResponse.<IntroSpectResponse>builder()
                .result(result)
                .build();
    }
}
