package com.naver.client.apicontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naver.client.common.BaseController;
import com.naver.client.mapper.ChatMessage;
import com.naver.client.resource.BaseResource;
import com.naver.client.resource.CommonResource;
import com.naver.client.service.ChatMessageService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CommonController extends BaseController {

	@Autowired
	ChatMessageService chatMessageService;

	@GetMapping("/check")
	public ResponseEntity invalidToken(@RequestHeader("Authorization") String token) {
		if (jwtTokenProvider.validateToken(token)) {
			int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));
			Map<String, Object> map = new HashMap<>();
			
			map.put("user", chatUserService.selectOneVo(userId));
			return ResponseEntity.ok(new CommonResource(OK_CODE, OK, map));

		}
		return ResponseEntity.badRequest().body(new BaseResource(Bad_Request_CODE, Bad_Request));

	}
}
