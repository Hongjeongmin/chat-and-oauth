package com.naver.client.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naver.client.repo.ImageRepo;

@Component
public class BasicImage {
	
	/*
	 * 1~7까지
	 */
	static public int getRandomInt() {
		int random = (int) (Math.random() * 7)+1;
		return random;
	}
}
