package com.example.qa;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket support configuration
 * <p>
 * Connection: WebSocket at "/ws", with SockJS fallback
 * <p>
 * Protocol: STOMP
 * <p>
 * Use internal simple messaging broker
 * <p>
 * IM: SEND to /im/send/{orderId}
 * SUBSCRIBE at /im/receive/{orderId}
 * <p>
 * Notification: SUBSCRIBE at /notification/{userId}
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // IM send message
        registry.setApplicationDestinationPrefixes("/im/send");
        // IM & Notification push message
        registry.enableSimpleBroker("/im/receive", "/notification");
    }
}
