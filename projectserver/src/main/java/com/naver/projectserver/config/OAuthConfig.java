package com.naver.projectserver.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@Configurable
@EnableWebSecurity
public class OAuthConfig extends WebSecurityConfigurerAdapter {
	
	
	@Autowired
	private DataSource dataSource;
	
    @Bean
    public TokenStore tokenStore() { //(2)
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public ApprovalStore approvalStore() { //(3)
        return new JdbcApprovalStore(dataSource);
    }


    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

	/*
	 * ADMIN 의 권한은 USER가 사용하는 권한을 다사용할 수 있어야한다. Spring Security 에서는
	 * AccessDecisionManager에다가 설정하고 authorizeReques.accessDecisionManager를 통해서 설정을
	 * 등록 할 수 있다. 설정이 다소 복잡하다.
	 */
	public SecurityExpressionHandler expressionHandler() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

		DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
		handler.setRoleHierarchy(roleHierarchy);

		return handler;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().anyRequest().permitAll();
		http.formLogin().loginPage("/login").permitAll();
		http.logout().logoutSuccessUrl("/");
	}
	
	
}
