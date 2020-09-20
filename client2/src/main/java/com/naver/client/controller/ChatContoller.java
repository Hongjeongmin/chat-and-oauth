package com.naver.client.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.naver.client.common.JwtTokenProvider;
import com.naver.client.dto.MessageDto;
import com.naver.client.mapper.ChatMember;
import com.naver.client.mapper.ChatMessage;
import com.naver.client.redis.RedisChatRoomRepo;
import com.naver.client.service.ChatMemberService;
import com.naver.client.service.ChatMessageService;
import com.naver.client.service.ChatService;
import com.naver.client.service.ChatUserService;
import com.naver.client.vo.ChatMemberVo;
import com.naver.client.vo.ChatMessageVo;
import com.naver.client.vo.ChatPrivateMemberVo;
import com.naver.client.vo.ChatVo;
import com.naver.client.vo.UpdateChatVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class ChatContoller {
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private SimpMessageSendingOperations messaginTemplate;

	@Autowired
	private ChatMessageService chatMessageService;

	@Autowired
	private ChatService chatService;
	@Autowired
	private ChatMemberService chatMemberService;

	@Autowired
	private RedisChatRoomRepo redisChatRoomRepo;
	
	@Autowired
	private ChatUserService chatUserService;

	/*
	 * 구룹 메시지 전송
	 */
	@MessageMapping("/chat/group/message")
	public void sendGroupMessage(MessageDto messageDto, StompHeaderAccessor header) {
		log.info("group/message");
		int userId = redisChatRoomRepo.getSessionInfo(header.getSessionId());
		log.info("userId {}", userId);
		ChatMessage chatMessage = modelMapper.map(messageDto, ChatMessage.class);
		chatMessage.setUserId(userId);

		/*
		 * unReadCount 계산 전체 멤버수 - 현재 채팅을 구독중인 수
		 */
		int chatId = chatMessage.getChatId();
		chatMessage.updateUnreadCnt(chatId, redisChatRoomRepo);
		log.info("sendMessage -> updateUnreadCnt");
		/*
		 * db 에 저장한다.
		 */
		chatMessageService.insert(chatMessage);

		/*
		 * 현재 채팅방에 참여한 인원의 readTime을 갱신시킨다.
		 */
		for (int i : redisChatRoomRepo.getChatMembers(chatId)) {
			chatMemberService.updateReadId(chatId, i, chatMessage.getId());
		}

		/*
		 * 소켓에 넣어둔다.
		 */
		ChatMessageVo chatMessageVo = modelMapper.map(chatMessage, ChatMessageVo.class);

		Map<String, Object> map = new HashMap<>();
		map.put("message", chatMessageVo);
		messaginTemplate.convertAndSend("/sub/chat/rooms/" + chatMessage.getChatId(), map);

		/*
		 * 채팅 목록을 보고있는 사용자에게 메세지를 날린다.
		 */

		int[] members = chatMemberService.selectChatMemberUserIds(chatId);

		UpdateChatVo updateChatVo = new UpdateChatVo();
		updateChatVo.setId(chatId);
		updateChatVo.setLastAt(chatMessage.getSentAt());
		updateChatVo.setLastMessage(chatMessage.getContent());

		for (int m : members) {
			if (m != userId) {
				updateChatVo.setUnreadCnt(chatMessageService.selectUnReadCount(chatId, m));
				chatService.existChatList(m, updateChatVo);
			}
		}

	}

	/*
	 * 1:1 메세지
	 */
	@MessageMapping("/chat/private/message")
	public void sendPrivateMessage(MessageDto messageDto, StompHeaderAccessor header) {
		log.info("private/message");
		int userId = redisChatRoomRepo.getSessionInfo(header.getSessionId());
		log.info("userId {}", userId);
		ChatMessage chatMessage = modelMapper.map(messageDto, ChatMessage.class);
		chatMessage.setUserId(userId);

		/*
		 * unReadCount 계산 전체 멤버수 - 현재 채팅을 구독중인 수
		 */
		int chatId = chatMessage.getChatId();
		chatMessage.updateUnreadCnt(chatId, redisChatRoomRepo);

		/*
		 * private message의 경우 상대방이 나갈시에 다시 활성화 해줘야한다.
		 */
		ChatPrivateMemberVo chatPrivate = chatMemberService.selectPrivateChatMemberView(userId, chatId);
		if (!chatPrivate.view) {
			/*
			 * 현재 상대방의 채팅방을 활성화 시키고 jointime을 지금의 메세지로 업데이트한다.
			 */
			ChatMember chatMember = new ChatMember(chatPrivate.getChatId(), chatPrivate.getYourId(), true);

			chatMemberService.updateJoin(chatMember);

			/*
			 * 상대가 참여하지 않았을 때 상대방이 현제 채팅목록 소켓에 있을경우 채팅방을 날려준다.
			 */
			ChatVo chatVo = new ChatVo();
			chatVo.setId(chatPrivate.getChatId());
			chatVo.setLastAt(chatMember.getJointime());
			chatVo.setType("PRIVATE");
			chatVo.setUnreadCnt(0);
			List<ChatMemberVo> members = chatUserService.selectChatMembers(chatId);
			chatVo.setMembers(members);
			
			chatService.newChatList(chatPrivate.getYourId(), chatVo);

		}
		/*
		 * db 에 저장한다.
		 */
		chatMessageService.insert(chatMessage);

		/*
		 * 현재 채팅방에 참여한 인원의 readTime을 갱신시킨다.
		 */
		for (int i : redisChatRoomRepo.getChatMembers(chatId)) {
			chatMemberService.updateReadId(chatId, i, chatMessage.getId());
		}

		/*
		 * 소켓에 넣어둔다.
		 */
		ChatMessageVo chatMessageVo = modelMapper.map(chatMessage, ChatMessageVo.class);

		Map<String, Object> map = new HashMap<>();
		map.put("message", chatMessageVo);
		messaginTemplate.convertAndSend("/sub/chat/rooms/" + chatMessage.getChatId(), map);

		
		/*
		 * 상대방에게 메세지도 날린다.
		 */
		UpdateChatVo updateChatVo = new UpdateChatVo();
		updateChatVo.setId(chatId);
		updateChatVo.setLastAt(chatMessage.getSentAt());
		updateChatVo.setLastMessage(chatMessage.getContent());
		updateChatVo.setUnreadCnt(chatMessageService.selectUnReadCount(chatId, chatPrivate.getYourId()));
		chatService.existChatList(chatPrivate.getYourId(), updateChatVo);

	}

	@MessageMapping("/chat/self/message")
	public void sendSelfMessage(MessageDto messageDto, StompHeaderAccessor header) {
		log.info("self/message");
		int userId = redisChatRoomRepo.getSessionInfo(header.getSessionId());
		log.info("userId {}", userId);
		ChatMessage chatMessage = modelMapper.map(messageDto, ChatMessage.class);
		chatMessage.setUserId(userId);

		/*
		 * unReadCount 계산 전체 멤버수 - 현재 채팅을 구독중인 수
		 */
		int chatId = chatMessage.getChatId();
		chatMessage.setUnreadCnt(0);
		/*
		 * db 에 저장한다.
		 */
		chatMessageService.insert(chatMessage);

		/*
		 * 마지막 구독한 메세지 업데이트한다.
		 */
		chatMemberService.updateReadId(chatId, userId, chatMessage.getId());

		/*
		 * 소켓에 넣어둔다.
		 */
		ChatMessageVo chatMessageVo = modelMapper.map(chatMessage, ChatMessageVo.class);

		Map<String, Object> map = new HashMap<>();
		map.put("message", chatMessageVo);
		messaginTemplate.convertAndSend("/sub/chat/rooms/" + chatMessage.getChatId(), map);

	}
}
