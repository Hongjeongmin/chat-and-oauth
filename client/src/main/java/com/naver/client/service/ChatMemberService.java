package com.naver.client.service;

import java.util.List;

import com.naver.client.mapper.ChatMember;

public interface ChatMemberService {
	/*
	 * insert chatMember table
	 */
	boolean insert(int chatId,int userId,int act);
	
	/*
	 * update chatMember act
	 */
	boolean updateAct(int chatId,int userId,int act);
	
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
	
	/*
	 * 채팅방에 있는 모든 멤버를 조회한다.
	 */
	int selectChatMemebersCnt(String chatId);
	
	/*
	 * 채팅의 참여한 유저의 가장 최근에 읽은 chatmessageId를 업데이트한다.
	 * (가장 나중에 말한 값으로)
	 */
	boolean updateReadId(int chatId, int userId);

	boolean updateReadId(int chatId, int userId, int lastreadId);
	
}
