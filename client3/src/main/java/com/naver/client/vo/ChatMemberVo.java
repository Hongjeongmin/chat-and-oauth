package com.naver.client.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMemberVo {
	int id;
	String name;
	String image;
	boolean active;
	
	public ChatMemberVo() {
		active =true;
	}
}
