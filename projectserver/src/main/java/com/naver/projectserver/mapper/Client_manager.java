package com.naver.projectserver.mapper;


import com.naver.projectserver.common.RandomValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor
public class Client_manager {
	String username;
	String client_id;
	String client_secret;
	String appname;
	String web_server_redirect_uri;
	
	public void Create_client_manger(String username,String appname, RandomValue randomValue,String web_server_redirect_uri) {
		this.username = username;
		this.appname =appname;
		this.client_id = randomValue.getRandomcode(60);
		this.client_secret = randomValue.getRandomcode(60);
		this.web_server_redirect_uri = web_server_redirect_uri;
	}

}
