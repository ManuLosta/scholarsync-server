package com.scholarsync.server.webSocket.config;

import com.scholarsync.server.repositories.SessionRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
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
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Autowired private SessionRepository sessionRepository;

  public WebSocketConfig() {}

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/global", "/individual");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/message-broker")
        .setAllowedOrigins("*")
        .withSockJS()
        .setInterceptors(
            new HttpSessionHandshakeInterceptor() {
              @Override
              public void afterHandshake(
                  @NonNull ServerHttpRequest request,
                  @NonNull ServerHttpResponse response,
                  @NonNull WebSocketHandler wsHandler,
                  Exception ex) {
                System.out.println("After Handshake");
                super.afterHandshake(request, response, wsHandler, ex);
              }

              @Override
              public boolean beforeHandshake(
                  @NonNull ServerHttpRequest request,
                  @NonNull ServerHttpResponse response,
                  @NonNull WebSocketHandler wsHandler,
                  @NonNull Map<String, Object> attributes)
                  throws Exception {
                System.out.println("Before Handshake");
                return super.beforeHandshake(request, response, wsHandler, attributes);
              }
            });
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        new ChannelInterceptor() {

          @Override
          public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
            StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            assert accessor != null;
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
              System.out.println("Connection attempt with token: " + authToken);
              return null;
            }
            return message;
          }

          @Override
          public void postSend(
              @NonNull Message<?> message, @NonNull MessageChannel channel, boolean sent) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
              System.out.println("New connection established.");
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
              System.out.println("Connection closed.");
            }
          }

          @Override
          public void afterSendCompletion(
              @NonNull Message<?> message,
              @NonNull MessageChannel channel,
              boolean sent,
              Exception ex) {
            if (ex != null) {
              ex.printStackTrace();
            }
          }
        });
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration.addDecoratorFactory(
        new WebSocketHandlerDecoratorFactory() {
          @Override
          @NonNull
          public WebSocketHandler decorate(@NonNull WebSocketHandler handler) {
            return new WebSocketHandlerDecorator(handler) {
              @Override
              public void afterConnectionEstablished(@NonNull WebSocketSession session)
                  throws Exception {
                // This will be invoked after WebSocket negotiation has succeeded and the WebSocket
                // connection is opened
                System.out.println("WebSocket connection opened");
                super.afterConnectionEstablished(session);
              }

              @Override
              public void afterConnectionClosed(
                  @NonNull WebSocketSession session, @NonNull CloseStatus closeStatus)
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
