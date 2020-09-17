package com.naver.client.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMember {
	int chatId;
	int userId;
	int lastreadId;
	boolean act;
	long jointime;

	public void update() {
		this.jointime = System.currentTimeMillis();
	}
}
