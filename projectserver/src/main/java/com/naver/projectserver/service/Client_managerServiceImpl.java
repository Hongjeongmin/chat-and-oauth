package com.naver.projectserver.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naver.projectserver.mapper.Client_manager;
import com.naver.projectserver.repo.Client_managerRepo;

@Service
public class Client_managerServiceImpl implements Client_managerService {

	@Autowired
	Client_managerRepo client_managerRepo;

	@Override
	public boolean insert(Client_manager cm) {
		return client_managerRepo.insert(cm);
	}

	@Override
	public boolean delete(String client_id) {
		return client_managerRepo.delete(client_id);
	}

	@Override
	public List<Client_manager> select(String username) {
		return client_managerRepo.select(username);
	}

	@Override
	public boolean updateAppname(Client_manager cm) {
		return client_managerRepo.updateAppname(cm);
	}

	@Override
	public boolean updateSecret(Client_manager cm) {
		// TODO Auto-generated method stub
		return client_managerRepo.updateSecret(cm);
	}

}
