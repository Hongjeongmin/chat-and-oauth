package com.naver.client.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class DaEun {
	@Value("${DaEun.client_id}")
	String client_id;
	@Value("${DaEun.secret}")
	String secret;
	@Value("${DaEun.redirect_uri}")
	String redirect_uri;
	@Value("${DaEun.target_uri}")
	String target_uri;
	@Value("${DaEun.scope}")
	String scope;
	@Value("${DaEun.api}")
	String api;
}
