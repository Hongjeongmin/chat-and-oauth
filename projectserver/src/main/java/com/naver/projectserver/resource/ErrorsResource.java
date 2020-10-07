package com.naver.projectserver.resource;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import lombok.Getter;

@Getter
public class ErrorsResource extends EntityModel<Errors> {
	Errors errors;
	
    public ErrorsResource(Errors errors) {
		this.errors = errors;
		//TODO 에러가 발생 하였을 때 에러이외애 참조할 수 있는 API 문서도 함께 반환한다.
	}

	@Override
	public EntityModel<Errors> add(Link... links) {
		return super.add(links);
	}
}
