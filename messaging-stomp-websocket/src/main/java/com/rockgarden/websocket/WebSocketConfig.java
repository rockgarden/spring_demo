package com.rockgarden.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket Configuration Created by rockgarden on 01/03/20.
 */
@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*
     * register a websocket endpoint that the clients will use to connect to
     * websocket server, enabling SockJS fallback options so that alternate
     * transports can be used if WebSocket is not available. The SockJS client will
     * attempt to connect to /gs-guide-websocket and use the best available
     * transport (websocket, xhr-streaming, xhr-polling, and so on).
     * "*" all origins are allowed
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    /*
     * Designates the /app prefix for messages that are bound for methods annotated
     * with @MessageMapping. This prefix will be used to define all the message
     * mappings. For example, /app/hello is the endpoint that the
     * GreetingController.greeting() method is mapped to handle.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");

        // Use this for enabling a Full featured broker like RabbitMQ.
        /*
         * registry.enableStompBrokerRelay("/topic") .setRelayHost("localhost")
         * .setRelayPort(61613) .setClientLogin("guest") .setClientPasscode("guest");
         */
    }

}