package com.scholarsync.server.controllers;

import com.scholarsync.server.services.GoogleOAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/google-auth")
public class GoogleAuthController {

  private final GoogleOAuthService googleOAuthService;
  private static final Logger log = LoggerFactory.getLogger(GoogleAuthController.class);

  public GoogleAuthController(GoogleOAuthService googleOAuthService) {
    this.googleOAuthService = googleOAuthService;
  }

  @GetMapping("/login")
  public RedirectView login(Principal principal) {
    if(principal == null) {
      log.error("Principal is null");
      return new RedirectView("http://localhost:5173/login-failed");
    }
    log.info("Principal class: {}", principal.getClass().getName());
    log.info("Principal details: {}", principal.toString());
    if (principal instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
      googleOAuthService.login(token);
    } else {
      log.error("Principal is not an instance of OAuth2AuthenticationToken");
    }
    return new RedirectView("http://localhost:5173");
  }
}