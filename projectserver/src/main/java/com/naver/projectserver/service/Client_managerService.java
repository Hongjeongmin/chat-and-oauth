package com.naver.projectserver.service;

import java.util.List;

import com.naver.projectserver.mapper.Client_manager;

public interface Client_managerService {
	boolean insert(Client_manager cm);
	boolean delete(String client_id);
	boolean updateAppname(Client_manager cm);
	boolean updateSecret(Client_manager cm);
	List<Client_manager> select(String username);
}
