package com.naver.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class OauthInfo {
	public String client_id;
	public String secret;
	public String redirect_uri;
	public String target_uri;
	public String scope;
	public String api;

	//TODO 설정파일에서 값을 가져올수 잇도록 변경한다.
	public OauthInfo(String server) {
		if ("WooJae".equals(server)) {
			this.client_id = "207c813d-3b10-4e06-b752-32f3da69d87f";
			this.secret = "331a74bd-d190-4038-8277-743461b3d1cf";
			this.redirect_uri = "http://localhost:8080/oauth/callback/WooJae";
			this.target_uri = "http://10.106.93.88:8090";
			this.scope = "name email phone";
			this.api = "/api/profile";
		} else if ("NaDan".equals(server)) {
			this.client_id = "3756e414-22b0-414d-8f19-f02e3b3b12c5";
			this.secret = "2161e111-abd7-400e-9c9b-4f0cdca2e4e4";
			this.redirect_uri = "http://localhost:8080/oauth/callback/NaDan";
			this.target_uri = "http://10.113.93.169:8080";
			this.scope = "name email phone";
			this.api = "/api/user";
		} else if ("JeongMin".equals(server)) {
			this.client_id = "M6vYDUHzrpy32G06qe3c8YY7Ehb8Eh1A9W1Qg6Tn4efvn4A442lF37o1TX9b";
			this.secret = "OiWpacIiG1HQ8wBtQy98BpHJf2HX9tW7Vxh64ZOnaft2QYSPhGZA116EWjMc";
			this.redirect_uri = "http://localhost:8080/oauth/callback/JeongMin";
			this.target_uri = "http://10.113.98.87:8080";
			this.scope = "read write trust";
			this.api = "/api/user";
		} else if ("DaEun".equals(server)) {
			this.client_id = "";
			this.secret = "";
			this.redirect_uri = "";
			this.target_uri = "";
			this.scope = "";
			this.api = "";
		}
	}

//	public void update(String server) {
//
//		if ("WooJae".equals(server)) {
//			mapping("WooJae");
//		} else if ("NaDan".equals(server)) {
//			mapping("NaDan");
//		} else if ("JeongMin".equals(server)) {
//			mapping("JeongMin");
//		} else if ("DaEun".equals(server)) {
//			mapping("DaEun");
//		}
//	}
// TODO 프로퍼티에서 가져오도록 변경해야한다.

//	private void mapping(String server) {
//		System.out.println("mapping");
//		this.client_id = environment.getProperty(server + ".client_id");
//		this.secret = environment.getProperty(server + ".secret");
//		this.redirect_uri = environment.getProperty(server + ".redirect_uri");
//		this.target_uri = environment.getProperty(server + ".target_uri");
//		this.scope = environment.getProperty(server + ".scope");
//	}

}
