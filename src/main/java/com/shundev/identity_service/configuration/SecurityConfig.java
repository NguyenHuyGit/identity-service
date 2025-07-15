package com.shundev.identity_service.configuration;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBIC_ENDPOINTS = { "/users", "/auth/token", "auth/introspect" };

    @Value("${jwt.signerKey}")
    protected String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // authorize http request
        httpSecurity.cors(withDefaults())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, PUBIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").permitAll()
                        .anyRequest().authenticated()); // allow access

        // need valid token to access endpoint
        httpSecurity.oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())) 
        );

        // disable CSRF
        httpSecurity.csrf(AbstractHttpConfigurer::disable); 

        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    };

    @Bean
    public CorsFilter corsFilter() {
        // config cors
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:3000/");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        // config cors for eandpoint
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration(("/**"), corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}