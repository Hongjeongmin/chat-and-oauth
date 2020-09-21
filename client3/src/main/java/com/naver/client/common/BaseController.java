package com.naver.client.common;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.naver.client.redis.RedisChatRoomRepo;
import com.naver.client.service.ChatMemberService;
import com.naver.client.service.ChatMessageService;
import com.naver.client.service.ChatService;
import com.naver.client.service.ChatUserService;
import com.naver.client.service.FriendService;

@CrossOrigin(origins = "*")
public class BaseController {
	
	public static final String OK = "OK";
	public static final int OK_CODE = 200;
	public static final String Bad_Request = "Bad Request";
	public static final int Bad_Request_CODE = 404;

	@Autowired
	public ModelMapper modelMapper;
	
	@Autowired
	public RedisChatRoomRepo redisChatRoomRepo;
	
	public static final int testId = 1;

	/*
	 * Service
	 */
	@Autowired
	public ChatService chatService;

	@Autowired
	public ChatUserService chatUserService;

	@Autowired
	public ChatMemberService chatMemberService;
	
	@Autowired
	public FriendService friendService;
	
	@Autowired
	public JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	public ChatMessageService chatMessageService;
	
	
}
