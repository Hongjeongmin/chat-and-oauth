package com.naver.client.mapper;

import com.naver.client.redis.RedisChatRoomRepo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
	/*
	 * IMOTICON : 이모티콘 TEXT : 일반 텍스트 메시지 NOTI : 공지 (입장,퇴장,초대)
	 */
	public enum TYPE {
		TEXT, IMOTICON, NOTI
	}

	int id;
	int chatId;
	int userId;
	String content;
	long sentAt;
	String type;
	int unreadCnt;

	/*
	 * 현재시간을 숫자로 저장한다.
	 */
	public void update() {
		sentAt = System.currentTimeMillis();
	}
	
	public void updateUnreadCnt(int chatId, RedisChatRoomRepo redisChatRoomRepo) {
		this.unreadCnt
		= redisChatRoomRepo.getChatCnt(chatId) - redisChatRoomRepo.getUserCountInChat(chatId);
		
	}
}