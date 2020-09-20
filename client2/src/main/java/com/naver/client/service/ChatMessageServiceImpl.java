package com.naver.client.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naver.client.mapper.ChatMessage;
import com.naver.client.mapper.MessageSearch;
import com.naver.client.repo.ChatMessageRepo;
import com.naver.client.vo.ChatMessageVo;

@Service
public class ChatMessageServiceImpl implements ChatMessageService{
	
	@Autowired
	ChatMessageRepo chatMessageRepo;
	
	@Override
	public boolean insert(ChatMessage chatMessage) {
		chatMessage.update();
		return chatMessageRepo.insert(chatMessage);
	}

	@Override
	public List<ChatMessageVo> selectMessageVoNoOption(MessageSearch messageSearch) {
		return chatMessageRepo.selectMessageVoNoOption(messageSearch);
	}

	@Override
	public List<ChatMessageVo> selectMessageVoLastMessageIdOption(MessageSearch messageSearch) {
		return chatMessageRepo.selectMessageVoLastMessageIdOption(messageSearch);
	}

	@Override
	public int selectUnReadCount(int chatId, int userId) {
		return chatMessageRepo.selectUnReadCount(chatId, userId);
	}

}
