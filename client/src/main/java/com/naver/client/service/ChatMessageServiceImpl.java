package com.naver.client.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naver.client.mapper.ChatMessage;
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
	public List<ChatMessageVo> selectMessageVoNoOption(int chatId, int size) {
		return chatMessageRepo.selectMessageVoNoOption(chatId, size);
	}

	@Override
	public List<ChatMessageVo> selectMessageVoLastMessageIdOption(int chatId, int size, int lastMessageId) {
		return chatMessageRepo.selectMessageVoLastMessageIdOption(chatId, size, lastMessageId);
	}

}
