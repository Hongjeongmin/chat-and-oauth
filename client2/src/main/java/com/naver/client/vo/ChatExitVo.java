package com.naver.client.vo;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatExitVo {
	String type;
	int userId;
	public ChatExitVo(int userId) {
		this.type = "EXIT";
		this.userId = userId;
	}
	
}
