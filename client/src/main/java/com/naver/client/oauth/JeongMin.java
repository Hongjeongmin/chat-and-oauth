package com.naver.client.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class JeongMin {
	@Value("${JeongMin.client_id}")
	String client_id;
	@Value("${JeongMin.secret}")
	String secret;
	@Value("${JeongMin.redirect_uri}")
	String redirect_uri;
	@Value("${JeongMin.target_uri}")
	String target_uri;
	@Value("${JeongMin.scope}")
	String scope;
	@Value("${JeongMin.api}")
	String api;
}
