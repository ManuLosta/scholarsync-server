package com.scholarsync.server.services;

import com.scholarsync.server.entities.GoogleUser;
import com.scholarsync.server.repositories.GoogleUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

@Service
public class GoogleOAuthService {

  @Autowired
  private GoogleUserRepository googleUserRepository;

  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  public void login(OAuth2AuthenticationToken token) {
    String googleId = token.getPrincipal().getAttribute("sub");
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            token.getAuthorizedClientRegistrationId(), token.getName());

    OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

    googleUserRepository.findByGoogleId(googleId).orElseGet(() -> {
      GoogleUser googleUser = new GoogleUser();
      googleUser.setGoogleId(googleId);
      googleUser.setEmail(token.getPrincipal().getAttribute("email"));
      assert refreshToken != null;
      googleUser.setRefreshToken(refreshToken.getTokenValue());
      return null;
    });
  }
}

