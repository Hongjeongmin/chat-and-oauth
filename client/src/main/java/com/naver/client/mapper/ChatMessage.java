package com.naver.client.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
	/*
	 * IMOTICON : 이모티콘
	 * TEXT : 일반 텍스트 메시지
	 * NOTI : 공지 (입장,퇴장,초대)
	 */
	public enum TYPE{
		TEXT,IMOTICON,NOTI
	}

	String type;
	int id;
	int chatId;
	int userId;
	String content;
	long sentAt;

	/*
	 * 현재시간을 숫자로 저장한다.
	 */
	public void update() {
		sentAt = System.currentTimeMillis();
	}
}