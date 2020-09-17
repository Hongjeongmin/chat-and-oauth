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
	
	/*
	 * 채팅방이 만들어진 시간 검색
	 */
	Long selectCreateTime(int id);
	
	/*
	 * 두사람 사이에 개인 채팅방이 존재하는가 ?
	 */
	Integer isExist(int myId, int yourId);

	/*
	 * chatId의 타입을 반환한다.
	 */
	String selectType(int chatId);

}
