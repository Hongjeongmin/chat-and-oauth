package com.naver.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MessageDto {
	String type;
	int chatId;
	String content;
}
