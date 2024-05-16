package com.scholarsync.server.webSocket.config;

import com.scholarsync.server.repositories.SessionRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final SessionRepository sessionRepository;

  public WebSocketConfig(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/global", "/individual");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/message-broker");
    registry.addEndpoint("/message-broker").withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        new ChannelInterceptor() {

          @Override
          public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
              String authToken = accessor.getFirstNativeHeader("Authorization");
              if (authToken != null && authToken.startsWith("Bearer ")) {
                String bearerToken = authToken.substring(7);
                if (sessionRepository.existsSessionById(bearerToken)) {
                  // If the token is valid, let the connection proceed
                  return message;
                }
              }
              // If the token is not valid, reject the connection by returning null
              return null;
            }
            return message;
          }
        });
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration.addDecoratorFactory(
        new WebSocketHandlerDecoratorFactory() {
          @Override
          public WebSocketHandler decorate(WebSocketHandler handler) {
            return new WebSocketHandlerDecorator(handler) {
              @Override
              public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                // This will be invoked after WebSocket negotiation has succeeded and the WebSocket
                // connection is opened
                System.out.println("WebSocket connection opened");
                super.afterConnectionEstablished(session);
              }

              @Override
              public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
                  throws Exception {
                // This will be invoked after the WebSocket connection is closed
                System.out.println("WebSocket connection closed");
                super.afterConnectionClosed(session, closeStatus);
              }
            };
          }
        });
  }
}
