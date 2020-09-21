package com.naver.client.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naver.client.mapper.ChatUser;
import com.naver.client.repo.ChatUserRepo;
import com.naver.client.vo.ChatMemberVo;
import com.naver.client.vo.ChatUserVo;

@Service
public class ChatUserServiceImpl implements ChatUserService {

	@Autowired
	ModelMapper modelMapper;
	@Autowired
	ChatUserRepo chatUserRepo;

	@Override
	public boolean insert(ChatUser chatUser) {
		return chatUserRepo.insert(chatUser);
	}

	@Override
	public ChatUser selectOne(int id) {
		return chatUserRepo.selectOne(id);
	}

	@Override
	public List<ChatUserVo> selectFriendVos(int id) {
		return chatUserRepo.selectFriendVos(id);
	}

	@Override
	public List<ChatUserVo> selectAllUserVos() {
		return chatUserRepo.selectAllUserVos();
	}

	@Override
	public ChatUserVo selectOneVo(int id) {
		return chatUserRepo.selectOneVo(id);
	}

	@Override
	public List<ChatMemberVo> selectIds(List<Integer> ids) {

		List<ChatMemberVo> members = new ArrayList<>();
		for (int id : ids) {
			members.add(modelMapper.map(chatUserRepo.selectOneVo(id), ChatMemberVo.class));
		}
		return members;
	}

	@Override
	public int selectIdAsUsername(String username) {
		return chatUserRepo.selectIdAsUsername(username);
	}

	@Override
	public String selectOneName(int id) {
		return chatUserRepo.selectOneName(id);
	}

	@Override
	public boolean updateImage(ChatUser chatUser) {
		return chatUserRepo.updateImage(chatUser);
	}

	@Override
	public List<String> selectInvitedUsers(int[] invitedIds) {
		List<String> list = new ArrayList<>();

		for (int invitedId : invitedIds) {
			list.add(chatUserRepo.selectOneName(invitedId));
		}
		return list;
	}

	@Override
	public List<ChatMemberVo> selectChatMembers(int chatId) {
		return chatUserRepo.selectChatMembers(chatId);
	}

}
