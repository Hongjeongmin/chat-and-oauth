package com.naver.projectserver.repo;

import com.naver.projectserver.mapper.Oauth_client_details;

public interface Oauth_client_detailsRepo {
	boolean insert(Oauth_client_details ocd);
	boolean update(Oauth_client_details ocd);
	boolean delete(String client_id);
	Oauth_client_details select();
}
