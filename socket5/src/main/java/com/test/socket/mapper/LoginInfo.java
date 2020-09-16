package com.test.socket.mapper;

import lombok.Builder;
import lombok.Getter;
/*
 * 로그인 정보(id 및 jwt토큰)를 전달할 DTO를 생성합니다.
 */
@Getter
public class LoginInfo {
	private String name;
	private String token;
	
	@Builder
	public LoginInfo(String name, String token) {
		this.name = name;
		this.token = token;
	}
}
