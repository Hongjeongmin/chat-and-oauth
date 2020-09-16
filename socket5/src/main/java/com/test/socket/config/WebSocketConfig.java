package com.test.socket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.test.socket.handler.StompHandler;

import lombok.RequiredArgsConstructor;

/*
 * stomp를 사용하기 위해 @EnalbeWebSocketMessageBroker을 선언
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	/*
	 * StompHandler는 token 유효성 검
	 */
	private final StompHandler stompHandler;
	
	/*
	 * pub/sub 메세징을 구현하기 위한 설정 1. pub : 메세지 발행 2. sub : 메세지 구독
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/sub");
		config.setApplicationDestinationPrefixes("/pub");
	}

	/*
	 * stomp Websocket의 연결 endpoint는 /ws-stomp로 설정 개발 서버 접속 주소 :
	 * ws://localhost:8080/ws-stomp
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws-stomp").setAllowedOrigins("*").withSockJS();
	}

	/*
	 * StompHandler가 Websocket 앞단에서 token을 체크할 수 있도록 다음과 같이 인터셉터로 설정합니다.
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompHandler);
	}

}
