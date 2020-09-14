package com.naver.client.repo;

import java.util.List;

import com.naver.client.mapper.ChatMessage;
import com.naver.client.mapper.LastMessageAndAt;
import com.naver.client.vo.ChatMessageVo;

public interface ChatMessageRepo {
	/*
	 * chatId로 가장 마지막 메시지와 시간을 반환합니다.
	 */
	LastMessageAndAt selectLastMessageAndAt(int chatId);
	
	/*
	 * 특정방의 특정 userId 읽지않은 메시지수 반환합니다.
	 */
	int selectUnReadCount(int chatId,int userId);
	
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
