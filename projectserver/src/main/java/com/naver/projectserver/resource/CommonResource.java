package com.naver.projectserver.resource;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor@AllArgsConstructor
@Getter@Setter
public class CommonResource {
	int code;
	String message;
	Map<String, Object> data;
}
