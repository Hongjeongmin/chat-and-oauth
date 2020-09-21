package com.naver.client.vo;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MessageVo {
	String type;
	int chatId;
	int userId;
	String content;
	long sentAt;
}
