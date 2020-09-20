package com.naver.client.service;

import java.util.List;

import com.naver.client.mapper.ChatMessage;
import com.naver.client.mapper.MessageSearch;
import com.naver.client.vo.ChatMessageVo;

public interface ChatMessageService {
	/*
	 * message 저장
	 */
	boolean insert(ChatMessage chatMessage);
	
	/*
	 * NoOptions
	 */
	List<ChatMessageVo> selectMessageVoNoOption(MessageSearch messageSearch);
	
	/*
	 * lastMessageId(optional)
	 */
	List<ChatMessageVo> selectMessageVoLastMessageIdOption(MessageSearch messageSearch);

	/*
	 * 특정방의 특정 userId 읽지않은 메시지수 반환합니다.
	 */
	int selectUnReadCount(int chatId,int userId);
	
}
