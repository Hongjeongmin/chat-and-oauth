package com.naver.client.service;

import java.util.List;

import com.naver.client.mapper.Chat;
import com.naver.client.vo.ChatUserVo;
import com.naver.client.vo.ChatVo;

public interface ChatService {
	/*
	 * chat 생성 
	 * chat table생성 후 chat 테이블의 참여자 members 생성
	 */
	boolean insert(Chat chat, List<Integer> members);
	
	/*
	 * chat 삭제
	 */
	boolean delete(int id);
	

	
	/*
	 * 채팅방에 모든 멤버들을 조회해서 반환한다.
	 */
	List<ChatUserVo> selectChatMembers(int chatId);
	
	/*
	 * 채팅 목록 조회
	 * userId 값으로 참여한 모든 채팅목록의 정보를 반환한다.
	 */
	
	List<ChatVo> selectChatList(int userId);

	
	
}
