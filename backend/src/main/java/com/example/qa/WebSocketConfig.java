package com.example.qa;

import com.example.qa.security.JwtUtils;
import com.example.qa.security.SecurityConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket support configuration
 * <p>
 * Connection: WebSocket at "/api/ws", with SockJS fallback
 * <p>
 * Protocol: STOMP
 * <p>
 * Use internal simple messaging broker
 * <p>
 * IM: SEND to /im/send/{orderId}
 * SUBSCRIBE at /im/receive/{orderId}
 * <p>
 * Notification: SUBSCRIBE at /notif/{userId}
 */
@Log4j2
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/ws");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // IM send message
        registry.setApplicationDestinationPrefixes("/im/send");
        // IM & Notification push message
        registry.enableSimpleBroker("/im/receive", "/notif");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    var token = accessor.getFirstNativeHeader(SecurityConstants.TOKEN_HEADER);
                    Authentication user = JwtUtils.getAuthentication(token);
                    if (user == null) {
                        return null;
                    }
                    user.setAuthenticated(true);
                    accessor.setUser(user);
                    log.info("User [id={}] has established a new WebSocket connection", user.getPrincipal());
                }
                return message;
            }
        });
    }
}
