package com.naver.client.service;

import java.util.List;

import com.naver.client.mapper.ChatMember;

public interface ChatMemberService {
	/*
	 * insert chatMember table
	 */
	boolean insert(int chatId,int userId);
	
	/*
	 * delete  chatMember table
	 */
	boolean delete(int chatId,int userId);
	
	/*
	 * 가장 최신으로 읽은 메시지의 시간으로 update
	 */
	boolean updateReadTime(ChatMember chatMember);
	
	/*
	 * chat.userId 로 참여한 모든 채팅방 조회
	 */
	List<Integer> selectChatUserIds(int userId);
	
}
