package com.naver.client.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {
	int id;
	String image;
	String name;
	String type;
	long createtime;

	public void update() {
		this.createtime = System.currentTimeMillis();
	}
}
