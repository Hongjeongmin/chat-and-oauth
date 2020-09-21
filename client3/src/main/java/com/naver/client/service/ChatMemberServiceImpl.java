package com.naver.client.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naver.client.mapper.ChatMember;
import com.naver.client.repo.ChatMemberRepo;
import com.naver.client.repo.ChatRepo;
import com.naver.client.vo.ChatPrivateMemberVo;

@Service
public class ChatMemberServiceImpl implements ChatMemberService{
	
	@Autowired
	ChatMemberRepo chatMemberRepo;
	
	@Autowired
	ChatRepo chatRepo;
	
	
	@Override
	public boolean insert(ChatMember chatMember) {
		return chatMemberRepo.insert(chatMember);
	}

	@Override
	public boolean delete(int chatId, int userId) {
		
		if("PRIVATE".equals(chatRepo.selectType(chatId))) {
			/*
			 * 1:1 채팅방의 경우 비활성화
			 */
			return chatMemberRepo.updateView(new ChatMember(chatId, userId, false));
		}
		
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

	@Override
	public boolean updateView(ChatMember chatMember) {
		return chatMemberRepo.updateView(chatMember);
	}

	@Override
	public int selectChatMemebersCnt(String chatId) {
		return chatMemberRepo.selectChatMemebersCnt(chatId);
	}

	@Override
	public boolean updateReadId(int chatId, int userId) {
		return chatMemberRepo.updateReadId(chatId,userId);
	}

	@Override
	public boolean updateReadId(int chatId, int userId, int lastreadId) {
		return chatMemberRepo.updateReadIdWithValue(chatId, userId, lastreadId);
	}

	@Override
	public ChatPrivateMemberVo selectPrivateChatMemberView(int userId, int chatId) {
		return chatMemberRepo.selectPrivateChatMemberView(userId,chatId);
	}

	@Override
	public boolean updateJoin(ChatMember chatMember) {
		return chatMemberRepo.updateJoin(chatMember);
	}

	@Override
	public long selectOneJointime(int userId, int chatId) {
		return chatMemberRepo.selectOneJointime(userId,chatId);
	}

	@Override
	public int[] selectChatMemberUserIds(int chatId) {
		return chatMemberRepo.selectChatMemberUserIds(chatId);
	}

}
