package com.naver.client.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naver.client.mapper.ChatMember;
import com.naver.client.repo.ChatMemberRepo;

@Service
public class ChatMemberServiceImpl implements ChatMemberService{
	
	@Autowired
	ChatMemberRepo chatMemberRepo;
	
	@Override
	public boolean insert(int chatId, int userId) {
		return chatMemberRepo.insert(chatId, userId);
	}

	@Override
	public boolean delete(int chatId, int userId) {
		return chatMemberRepo.delete(chatId, userId);
	}

	@Override
	public boolean updateReadTime(ChatMember chatMember) {
		return chatMemberRepo.updateReadTime(chatMember);
	}

	@Override
	public List<Integer> selectChatUserIds(int userId) {
		return chatMemberRepo.selectChatUserIds(userId);
	}

}
