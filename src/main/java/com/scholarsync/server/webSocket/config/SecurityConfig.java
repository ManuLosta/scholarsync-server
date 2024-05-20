//package com.scholarsync.server.webSocket.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//  @Bean
//  @Order(1)
//  public SecurityFilterChain webSocketSecurityFilterChain(HttpSecurity http) throws Exception {
//    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
//    http.authorizeHttpRequests((authorizeRequests) -> authorizeRequests.anyRequest().permitAll());
//    return http.build();
//  }
//}
