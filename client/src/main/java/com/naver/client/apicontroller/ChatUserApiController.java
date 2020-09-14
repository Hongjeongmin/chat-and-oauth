package com.naver.client.apicontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naver.client.common.BaseController;
import com.naver.client.resource.CommonResource;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class ChatUserApiController extends BaseController {
	@GetMapping
	public ResponseEntity searchAllUsers(@RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<>();
		map.put("users", chatUserService.selectAllUserVos());
		
		return ResponseEntity.ok( new CommonResource(OK_CODE,OK,map));
	}
}
