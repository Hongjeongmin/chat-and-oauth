package com.naver.client.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatDto {
	String image;
	String name;
	String type;
	List<Integer> friendIds;
}
