package com.naver.client.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateChatVo {
	int id;
	String lastMessage;
	long lastAt;
	int unreadCnt;
}
