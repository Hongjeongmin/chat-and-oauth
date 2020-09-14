package com.naver.client.service;

import java.util.List;

import com.naver.client.vo.ChatUserVo;

public interface FriendService {
	/*
	 * insert
	 */
	boolean insert(int userId, int friendId);

	/*
	 * delete
	 */
	boolean delete(int userId, int friendId);

	/*
	 * selectFriends 내가 친추한 모든 userId 를 반환한다.
	 */
	List<ChatUserVo> selectFriendVos(int userId);
}
