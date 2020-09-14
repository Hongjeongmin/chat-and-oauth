package com.naver.client.resource;

import lombok.Getter;
import lombok.Setter;


@Getter@Setter
public class CommonResource extends BaseResource{
	Object data;
	
	public CommonResource(int code, String message, Object data) {
		super(code, message);
		this.data = data;
	}

}
