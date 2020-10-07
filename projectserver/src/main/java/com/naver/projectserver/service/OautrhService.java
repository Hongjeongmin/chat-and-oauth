package com.naver.projectserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/*
 * Spring Security는 로그인시 Spring Security에 맞는 form으로 값을 설정해주면
 * SecurityContextHolder -> SecurityContext -> Authentication에 자동으로 설정 (서블릿 기반 REST API요청시에는 매번 로그인페이지로 이동)
 * Principal과 GrantAuthority 제공
 */


@Service
public class OautrhService implements UserDetailsService{
	
	@Autowired
	UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		com.naver.projectserver.mapper.User user = userService.login(username);
		
		
		if(user == null) {
			throw new UsernameNotFoundException(username);
		}
		//admin 아이디만 권한을 준다.
//		String role = "admin".equals(user.getId()) ? "ADMIN" : "USER";
		return User.builder()
				.username(user.getUsername())
				.password(user.getPwd())
				.roles("USER")
				.build();
		
	}

}
