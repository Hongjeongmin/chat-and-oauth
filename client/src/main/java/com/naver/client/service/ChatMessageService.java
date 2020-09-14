package com.naver.client.service;

import java.util.List;

import com.naver.client.mapper.ChatMessage;
import com.naver.client.vo.ChatMessageVo;

public interface ChatMessageService {
	/*
	 * message 저장
	 */
	boolean insert(ChatMessage chatMessage);
	
	/*
	 * NoOptions
	 */
	List<ChatMessageVo> selectMessageVoNoOption(int chatId,int size);
	
	/*
	 * lastMessageId(optional)
	 */
	List<ChatMessageVo> selectMessageVoLastMessageIdOption(int chatId,int size,int lastMessageId);
}
