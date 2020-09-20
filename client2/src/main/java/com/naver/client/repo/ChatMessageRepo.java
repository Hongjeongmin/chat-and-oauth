package com.naver.client.repo;

import java.util.List;

import com.naver.client.mapper.ChatMessage;
import com.naver.client.mapper.LastMessageAndAt;
import com.naver.client.mapper.MessageSearch;
import com.naver.client.vo.ChatMessageVo;
import com.naver.client.vo.UnReadCountVo;

public interface ChatMessageRepo {
	/*
	 * chatId로 가장 마지막 메시지와 시간을 반환합니다.
	 */
	LastMessageAndAt selectLastMessageAndAt(int chatId,int userId);
	
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
	List<ChatMessageVo> selectMessageVoNoOption(MessageSearch messageSearch);
	
	/*
	 * lastMessageId(optional)
	 */
	List<ChatMessageVo> selectMessageVoLastMessageIdOption(MessageSearch messageSearch);

	/*
	 * 읽지않은 메세지들을 조회한다.
	 */
	UnReadCountVo[] selectUnReadMessags(int userId, int chatId);

	

	
}
