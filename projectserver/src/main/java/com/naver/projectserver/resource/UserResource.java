package com.naver.projectserver.resource;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.naver.projectserver.controller.UserController;
import com.naver.projectserver.mapper.User;

import lombok.Getter;

@Getter
public class UserResource extends EntityModel<User>{
	
	@JsonUnwrapped
	User user;

	public UserResource(User user) {
		this.user = user;
		this.add(linkTo(UserController.class).withSelfRel());
	}

}
