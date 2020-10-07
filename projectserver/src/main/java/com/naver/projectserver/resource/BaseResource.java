package com.naver.projectserver.resource;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import lombok.Getter;

@Getter
public class BaseResource extends EntityModel<String>{
	String status;
	/*
	 * 생성자 생성시 현재 상태를 난타내는 status	와 어떤 상태로 이전할지에 대한 하이퍼링크를 추가한다.
	 */
	public BaseResource(String status,Link... links ) {
		this.status = status;
		this.add(links);
	}
}
