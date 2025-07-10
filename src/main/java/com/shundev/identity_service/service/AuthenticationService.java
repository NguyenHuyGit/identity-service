package com.shundev.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shundev.identity_service.dto.request.AuthenticationRequest;
import com.shundev.identity_service.dto.request.IntroSpectRequest;
import com.shundev.identity_service.dto.response.AuthenticationResponse;
import com.shundev.identity_service.dto.response.IntroSpectResponse;
import com.shundev.identity_service.exception.AppException;
import com.shundev.identity_service.exception.ErrorCode;
import com.shundev.identity_service.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    IUserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SINGER_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        // if log-in fail
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // generate a token
        var token = generateToken(request.getUsername());
        return AuthenticationResponse
                .builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }

    // Generat token
    private String generateToken(String username) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("shundev.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        // sign
        try {
            jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException ex) {
            log.error("Cannnot create token");
            throw new RuntimeException(ex);
        }

    }

    public IntroSpectResponse introspect (IntroSpectRequest request)
            throws JOSEException, ParseException {
        log.info("start introspect");
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes()); // json web signature

        SignedJWT signedJWT = SignedJWT.parse(token);

        // check expiry time of token
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime(); // get through by claim set

        var isVerified = signedJWT.verify(verifier);

        log.info("end introspect");
        return IntroSpectResponse.builder()
                .valid(isVerified && expiryTime.after(new Date())) // valida check  two condition 
                .build();
    }
}
