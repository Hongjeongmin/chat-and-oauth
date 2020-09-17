package com.naver.client.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatUser {
	int id; 
	String username;
	String name; 
	String image; 
	String access_token; 
	String refresh_token;
}
