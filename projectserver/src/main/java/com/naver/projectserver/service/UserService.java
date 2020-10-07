package com.naver.projectserver.service;

import com.naver.projectserver.mapper.User;

public interface UserService {
	User login(String username);
	boolean signup(User user);
	public boolean update(User user);
	public User select(String username);
	public boolean updateNickname(User user);
}
