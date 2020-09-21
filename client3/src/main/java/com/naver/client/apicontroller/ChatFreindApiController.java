package com.naver.client.apicontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naver.client.common.BaseController;
import com.naver.client.dto.IdDto;
import com.naver.client.resource.BaseResource;
import com.naver.client.resource.CommonResource;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
public class ChatFreindApiController extends BaseController {

	@GetMapping
	public ResponseEntity searchFriends(@RequestHeader("Authorization") String token) {
		log.info("searchFriends");
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("friends", friendService.selectFriendCahtUserVos(userId));
		
		return ResponseEntity.ok(new CommonResource(OK_CODE, OK,map));
	}

	@PostMapping
	public ResponseEntity createFriend(@RequestHeader("Authorization") String token,@RequestBody IdDto idDto) {
		log.info("createFriend");
		BaseResource resource = null;
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));
		int friendId = idDto.getId();
		
		try {
			friendService.insert(userId, friendId);
			return ResponseEntity.ok(new BaseResource(OK_CODE,OK));
			
		}catch(Exception e) {
			return ResponseEntity.ok(new BaseResource(Bad_Request_CODE,Bad_Request));
		}
	}

	@DeleteMapping("/{friendId}")
	public ResponseEntity deleteFriend(@RequestHeader("Authorization") String token,@PathVariable("friendId") int friendId) {
		log.info("deleteFriend");
		BaseResource resource = new BaseResource(OK_CODE, OK);
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		if (friendService.delete(userId, friendId))
			return ResponseEntity.ok(new BaseResource(OK_CODE, OK));

		return ResponseEntity.ok(new BaseResource(Bad_Request_CODE, Bad_Request));
	}
}
