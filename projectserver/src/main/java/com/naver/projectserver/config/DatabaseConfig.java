package com.naver.projectserver.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * repo를 Bean에다가 등록한다.
 */

@Configuration
@MapperScan(basePackages = "com.naver.projectserver.repo")
public class DatabaseConfig {
}
