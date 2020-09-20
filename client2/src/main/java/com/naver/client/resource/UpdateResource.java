package com.naver.client.resource;

import java.util.Map;

public class UpdateResource {
	String type;
	Map<String, Object> chat;
	public UpdateResource(String type, Map chat) {
		this.type = type;
		this.chat = chat;
	}
}
