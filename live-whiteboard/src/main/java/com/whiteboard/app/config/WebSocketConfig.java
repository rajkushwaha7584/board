package com.whiteboard.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP WebSocket configuration for Spring Boot.
 * <ul>
 *   <li>Application prefix: /app — messages sent to /app/* are routed to @MessageMapping methods</li>
 *   <li>Broker prefix: /topic — messages broadcast to subscribers of /topic/*</li>
 * </ul>
 * Clients connect to the /ws endpoint (with SockJS) and subscribe to /topic/... for broadcasts.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /** STOMP endpoint for WebSocket connections (use with SockJS). */
    public static final String WS_ENDPOINT = "/ws";
    /** Application destination prefix — client sends to /app/... */
    public static final String APP_PREFIX = "/app";
    /** Topic prefix for broker broadcasts — client subscribes to /topic/... */
    public static final String TOPIC_PREFIX = "/topic";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(TOPIC_PREFIX);           // broadcast to /topic/*
        config.setApplicationDestinationPrefixes(APP_PREFIX); // route to /app/*
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WS_ENDPOINT)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
