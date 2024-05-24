package com.scholarsync.server.webSocket.config;

import com.scholarsync.server.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Autowired private SessionRepository sessionRepository;

  public WebSocketConfig() {}

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/global", "/individual", "/queue", "/chat");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/message-broker").setAllowedOrigins("*").withSockJS();
  }

  @Override
  public void configureClientOutboundChannel(ChannelRegistration registration) {
    registration.interceptors(
            new ChannelInterceptor() {
              @Override
              public void postSend(@NonNull Message<?> message, @NonNull MessageChannel channel, boolean sent) {
                String destination = (String) message.getHeaders().get("simpDestination");
                System.out.println("Message sent to topic: " + destination);
              }
            });
  }


}
