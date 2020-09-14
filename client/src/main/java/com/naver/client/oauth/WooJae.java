package com.naver.client.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class WooJae {
	@Value("${WooJae.client_id}")
	String client_id;
	@Value("${WooJae.secret}")
	String secret;
	@Value("${WooJae.redirect_uri}")
	String redirect_uri;
	@Value("${WooJae.target_uri}")
	String target_uri;
	@Value("${WooJae.scope}")
	String scope;
	@Value("${WooJae.api}")
	String api;
}
