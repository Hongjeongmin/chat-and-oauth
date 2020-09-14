package com.naver.client.repo;

import java.util.List;

import com.naver.client.mapper.Chat;
import com.naver.client.mapper.ChatUser;
import com.naver.client.vo.ChatVo;

public interface ChatRepo {
	/*
	 * insert chat
	 */
	boolean insert(Chat chat);
	
	/*
	 * delete chat
	 */
	boolean delete(int id);
	
	/*
	 * Chat 형태로 chat을 반환한다
	 * id ,chatImage, chatName ,type 반환
	 */
	Chat selectOne(int id);
	
	/*
	 * 참여한 채팅방 모두 조회
	 */
	
	List<ChatVo> selectAllChatAsUserId(int userId);

}
