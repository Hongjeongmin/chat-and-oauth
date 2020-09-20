package com.naver.client.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MessageSearch {
	int chatId;
	int size;
	long jointime;
	int lastMessageId;
}
