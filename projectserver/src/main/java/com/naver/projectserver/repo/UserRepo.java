package com.naver.projectserver.repo;

import com.naver.projectserver.mapper.User;

public interface UserRepo {
	public User login(String id);
	public User select(String id);
	public boolean signup(User user);
	public boolean update(User user);
	public boolean updateNickname(User user);
}
