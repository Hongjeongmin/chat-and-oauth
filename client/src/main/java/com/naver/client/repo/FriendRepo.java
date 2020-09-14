package com.naver.client.repo;

import java.util.List;

import com.naver.client.vo.ChatUserVo;

public interface FriendRepo {
	/*
	 * insert
	 */
	boolean insert(int userId, int friendId);

	/*
	 * delete
	 */
	boolean delete(int userId, int friendId);
	
	/*
	 * selectFriends 
	 * 내가 친추한 모든 userId 를 반환한다.
	 */
	List<Integer> selectFriends(int userId);
	
	/*
	 * userId를 기준으로 내가 친추한 모든 친구의 이름을 반환한다.
	 */
	List<String> selectFriendNames(int userId);
	
	/*
	 * userId를 기준으로 내가 친추한 모든 친구의 chatUserVo를 반환한다.
	 */
	List<ChatUserVo> selectFriendCahtUserVos(int userId);
}
