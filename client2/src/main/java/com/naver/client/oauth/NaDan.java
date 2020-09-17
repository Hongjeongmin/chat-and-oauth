package com.naver.client.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class NaDan {
	@Value("${NaDan.client_id}")
	String client_id;
	@Value("${NaDan.secret}")
	String secret;
	@Value("${NaDan.redirect_uri}")
	String redirect_uri;
	@Value("${NaDan.target_uri}")
	String target_uri;
	@Value("${NaDan.scope}")
	String scope;
	@Value("${NaDan.api}")
	String api;
}
