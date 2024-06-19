package com.scholarsync.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {



  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(
            httpSecurityCorsConfigurer -> {
              CorsConfigurationSource corsConfigurationSource = corsConfigurationSource();
            })
            .csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(
            (authorizeRequests) ->
                authorizeRequests.requestMatchers("/**").permitAll());
    http.oauth2Login(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:5173"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    return new InMemoryClientRegistrationRepository(ClientRegistration.withRegistrationId("google")
            .clientId("63087875898-erguna3m9pbnfh2anj52vu3k1hif6h0e.apps.googleusercontent.com")
            .clientSecret("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")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:8080/api/v1/google-auth/login")
            .scope("openid", "profile", "email")
            .authorizationUri("https://accounts.google.com/o/oauth2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .clientName("Google")
            .build());
  }

}
