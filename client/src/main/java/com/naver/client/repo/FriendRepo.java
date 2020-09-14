package com.naver.client.repo;

import java.util.List;

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
	
}
