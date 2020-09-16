package com.test.socket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*
 * Web Security 설정
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		 * frameOptions().sameOrigin() 설정은 SocketJS는 기본적으로 HTML ifram 요소를 통한 전송을
		 * 허용하지 않도록 설정되는데 해당 내용을 해제한다.
		 */
		http
			.csrf().disable()
			.headers()
				.frameOptions().sameOrigin()
			.and()
				.formLogin()
			.and()
				.authorizeRequests()
					.antMatchers("/chat/**").hasRole("USER")
					.anyRequest().permitAll();
	}
	/*
	 * DataBase는 사용하지 않고 In - Memory를 이용한다.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	
		auth.inMemoryAuthentication()
			.withUser("user1")
			.password("{noop}1")
			.roles("USER")
		.and()
			.withUser("user2")
			.password("{noop}1")
			.roles("USER")
		.and()
			.withUser("guest")
			.password("{noop}1")
			.roles("GUEST");
		
	}
	
	
	
}
