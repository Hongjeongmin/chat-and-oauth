package com.naver.client.resource;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BaseResource {
	int code;
	String message;
	
	public BaseResource(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
}
