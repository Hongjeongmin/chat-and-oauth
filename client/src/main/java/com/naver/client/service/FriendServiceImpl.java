package com.naver.client.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naver.client.repo.ChatUserRepo;
import com.naver.client.repo.FriendRepo;
import com.naver.client.vo.ChatUserVo;

@Service
public class FriendServiceImpl implements FriendService{
	
	@Autowired
	private FriendRepo friendRepo;
	
	@Autowired
	private ChatUserRepo chatUserRepo;
	
	@Override
	public boolean insert(int userId, int friendId) {
		return friendRepo.insert(userId, friendId);
	}

	@Override
	public boolean delete(int userId, int friendId) {
		return friendRepo.delete(userId, friendId);
	}

	@Override
	public List<ChatUserVo> selectFriendVos(int userId) {
		List<Integer> friendIds = friendRepo.selectFriends(userId);
		List<ChatUserVo>friendVos = new ArrayList<>();
		
		for(int id : friendIds) {
			friendVos.add(chatUserRepo.selectOneVo(id));
		}
		
		return friendVos;
		
	}

}
