package com.fth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 前端连接的入口地址（支持跨域）
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 生产环境应限制域名
                .withSockJS(); // 兼容不支持 WebSocket 的浏览器（可选）
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端订阅的前缀
        registry.enableSimpleBroker("/topic", "/queue"); // 广播 or 私聊
        // 客户端发送消息的前缀
        registry.setApplicationDestinationPrefixes("/app");
    }
}