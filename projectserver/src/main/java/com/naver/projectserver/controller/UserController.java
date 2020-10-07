package com.naver.projectserver.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naver.projectserver.dto.UserDto;
import com.naver.projectserver.dto.UserSearchDto;
import com.naver.projectserver.mapper.User;
import com.naver.projectserver.resource.BaseResource;
import com.naver.projectserver.resource.UserResource;
import com.naver.projectserver.service.UserService;

@RequestMapping("/api/user")
@RestController
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	ModelMapper modelMapper;

	/*
	 * access_Token 등록된 User를 반환합니다.
	 */
	@GetMapping
	public ResponseEntity<UserSearchDto> searchUser(Principal principal) {

		User user = userService.select(principal.getName());

		UserSearchDto userSearchDto = modelMapper.map(user, UserSearchDto.class);
		userSearchDto.setId(user.getUsername());
		
		return ResponseEntity.ok().body(userSearchDto);
	}

	/*
	 * access_Token 등록된 User의 nickanme을 update합니다.
	 */

	@PutMapping
	public ResponseEntity updateUser(Principal principal, @RequestBody UserDto userDto) {
		User user = userService.login(principal.getName());

		user.setName(userDto.getName());
		if (userService.updateNickname(user)) {
			UserResource userResource = new UserResource(user);
			return ResponseEntity.ok(userResource);
		}

		BaseResource baseResource = new BaseResource("failed", linkTo(UserController.class).withSelfRel());

		return ResponseEntity.badRequest().body(baseResource);

	}

}
