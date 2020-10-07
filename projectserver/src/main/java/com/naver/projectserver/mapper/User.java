package com.naver.projectserver.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor@NoArgsConstructor
@Getter@Setter@Builder
public class User {
	int id;
	String username;
	String pwd;
	String name;
	String phone;
	String email;
	public void encodePassword(PasswordEncoder passwordEncoder) {
		this.pwd = passwordEncoder.encode(this.pwd);
	}
}
