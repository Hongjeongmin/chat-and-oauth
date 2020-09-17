package com.naver.client.repo;

import java.util.List;

import com.naver.client.mapper.ChatMember;

public interface ChatMemberRepo {
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
	 * chat.chatId 로 참여한 모든 멤버 조회
	 */
	List<Integer> selectChatMemeberIds(int chatId);
	
	/*
	 * 채팅방에 있는 모든 멤버의 수 조회
	 */
	int selectChatMemebersCnt(String chatId);
	
	/*
	 * 채팅방의 유저가 가장 마지막에 읽은 메시지 아이디를 업데이트 한다.
	 * 
	 */
	boolean updateReadId(int chatId, int userId);

	boolean updateReadIdWithValue(int chatId, int userId, int lastreadId);
	

}
