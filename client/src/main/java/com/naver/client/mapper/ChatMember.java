package com.naver.client.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatMember {
	int chatId; 
	int userId;
	int readtime;
}
