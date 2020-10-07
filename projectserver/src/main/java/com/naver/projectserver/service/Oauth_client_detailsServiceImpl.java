package com.naver.projectserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.naver.projectserver.mapper.Oauth_client_details;
import com.naver.projectserver.repo.Oauth_client_detailsRepo;

@Service
public class Oauth_client_detailsServiceImpl implements Oauth_client_detailsService {
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	Oauth_client_detailsRepo odsRepo;
	
	@Override
	public boolean insert(Oauth_client_details ocd) {
		ocd.encodeClient_secret(passwordEncoder);
		return odsRepo.insert(ocd);
	}

	@Override
	public Oauth_client_details select() {
		return odsRepo.select();
	}

	@Override
	public boolean update(Oauth_client_details ocd) {
		ocd.encodeClient_secret(passwordEncoder);
		return odsRepo.update(ocd);
	}

	@Override
	public boolean delete(String client_id) {
		
		return odsRepo.delete(client_id);
	}

}
