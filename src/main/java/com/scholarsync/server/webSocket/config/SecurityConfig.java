package com.scholarsync.server.webSocket.config;

import com.scholarsync.server.filters.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired private TokenFilter tokenFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(
            cors ->
                cors.configurationSource(
                    request ->
                        new CorsConfiguration().applyPermitDefaultValues())) // Allow same origin
        .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/message-broker/**")
                    .permitAll() // Exclude WebSocket endpoints
                    .anyRequest()
                    .permitAll())
        .httpBasic(AbstractHttpConfigurer::disable) // Disable basic HTTP auth
        .addFilterBefore(tokenFilter, LogoutFilter.class); // Add your custom filter
    return http.build();
  }
}
