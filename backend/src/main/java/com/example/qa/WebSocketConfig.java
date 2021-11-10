package com.example.qa;

import com.example.qa.errorhandling.MessageException;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

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
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/ws")
                .setAllowedOriginPatterns("https://*.app.secoder.net/", "http://localhost:[*]")
                .withSockJS();
        registry.setErrorHandler(new StompSubProtocolErrorHandler() {
            @Nullable
            @Override
            public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
                if (ex.getCause() instanceof MessageException e) {
                    // Simply unwrap to MessageException
                    return super.handleClientMessageProcessingError(clientMessage, e);
                } else {
                    return super.handleClientMessageProcessingError(clientMessage, ex);
                }
            }
        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // IM send message
        registry.setApplicationDestinationPrefixes("/im/send");
        // IM & Notification push message
        registry.enableSimpleBroker("/im/receive", "/notif");
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("/im/send/*").authenticated()
                .simpSubscribeDestMatchers("/im/receive/*", "/notif/*").authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
