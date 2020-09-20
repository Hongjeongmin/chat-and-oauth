package com.naver.client.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMember {
	int chatId;
	int userId;
	int lastreadId;
	boolean view;
	long jointime;
	boolean active;
	public void update() {
		jointime = System.currentTimeMillis();
	}
	public ChatMember(int chatId, int userId, boolean view) {
		this.chatId = chatId;
		this.userId = userId;
		this.view = view;
		update();
	}
}
