package com.naver.projectserver.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Oauth_client_details {

	private String client_id;
	private String resource_ids;
	private String client_secret;
	private String scope;
	private String authorized_grant_types;
	private String web_server_redirect_uri;
	private String authorities;
	private String access_token_validity;
	private String refresh_token_validity;
	private String additional_information;
	private String autoapprove;
	/*
	 * 기본 생성시 access_token 과 refresh_token의 유효시간을 설정합니다.
	 * TODO 이값을 다른 곳에서 관리 할 수 있도록 합니다.
	 */
	public Oauth_client_details() {
		this.access_token_validity = "7200";
		this.refresh_token_validity = "2160000";
		this.autoapprove = "false";
	}

	public void encodeClient_secret(PasswordEncoder passwordEncoder) {
		this.client_secret = passwordEncoder.encode(this.client_secret);
	}

}
