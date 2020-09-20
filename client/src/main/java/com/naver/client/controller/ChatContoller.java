package com.naver.client.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.naver.client.common.JwtTokenProvider;
import com.naver.client.dto.ChatExitDto;
import com.naver.client.dto.ChatJoinDto;
import com.naver.client.dto.MessageDto;
import com.naver.client.mapper.ChatMessage;
import com.naver.client.redis.RedisChatRoomRepo;
import com.naver.client.service.ChatMemberService;
import com.naver.client.service.ChatMessageService;
import com.naver.client.service.ChatUserService;
import com.naver.client.vo.ChatMessageVo;
import com.naver.client.vo.MessageVo;

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
	private ChatUserService chatUserService;

	@Autowired
	private ChatMemberService chatmemberService;

	@Autowired
	private RedisChatRoomRepo redisChatRoomRepo;

	/*
	 * 채팅방 참여
	 */
	@MessageMapping("/chat/join")
	public void chatJoin(ChatJoinDto chatJoinDto, @Header("Authorization") String token) {
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));
		/*
		 * chatJoinDto.invitedIds [number] -> id로 바꾼다.
		 */

		/*
		 * ex) HongJeongMin님이 HongNaDan, KimWooJae, ParkDaEn 님을 초대하였습니다.
		 */
		List<String> invitedUsers = chatUserService.selectInvitedUsers(chatJoinDto.getInvitedIds());

		StringBuilder content = new StringBuilder();
		content.append(chatUserService.selectOneName(userId)).append("님이 ");
		content.append(String.join(", ", invitedUsers)).append("을 초대하였습니다.");

		/*
		 * 채팅방 초대 메시지 전송.
		 */

		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setChatId(chatJoinDto.getChatId());
		chatMessage.setContent(content.toString());
		chatMessage.setType("NOTI");

		/*
		 * DB에 저장
		 */
		chatMessageService.insert(chatMessage);
		Map<String, Object> map = new HashMap<>();
		map.put("message", modelMapper.map(chatMessage, MessageVo.class));
		messaginTemplate.convertAndSend("/sub/chat/rooms/" + chatJoinDto.getChatId(), map);

		/*
		 * TODO : Chat Member 활성화
		 */
		for (int m : chatJoinDto.getInvitedIds()) {
			chatmemberService.updateAct(chatJoinDto.getChatId(), m, 1);
		}

	}

//	/*
//	 * 채팅방 나가기
//	 */
//	@MessageMapping("/chat/exit")
//	public void chatExit(ChatExitDto chatExitDto, @Header("Authorization") String token) {
//		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));
//
//		/*
//		 * ex) HongJeongMin님이 나갔습니다.
//		 */
//
//		String content = chatUserService.selectOneName(userId) + "님이 나갔습니다.";
//		/*
//		 * 메시지 전송
//		 */
//
//		ChatMessage chatMessage = modelMapper.map(chatExitDto, ChatMessage.class);
//		chatMessage.setType("NOTI");
//		chatMessage.setContent(content);
//		chatMessage.setUserId(userId);
//		/*
//		 * DB 저장
//		 */
//		chatMessageService.insert(chatMessage);
//
//		/*
//		 * 소켓에 저장
//		 */
//		messaginTemplate.convertAndSend("/sub/chat/rooms/" + chatMessage.getChatId(),
//				modelMapper.map(chatMessage, MessageVo.class));
//
//		// TODO 나가기 pub 신청시 삭제할 건지 아니면 따로 httpAPI 호출할건지 미정.
//	}

	/*
	 * 일반 메시지 전송
	 */
	@MessageMapping("/chat/message")
	public void sendMessage(MessageDto messageDto, StompHeaderAccessor header) {

		int userId = redisChatRoomRepo.getSessionInfo(header.getSessionId());
		ChatMessage chatMessage = modelMapper.map(messageDto, ChatMessage.class);
		chatMessage.setUserId(userId);

		/*
		 * unReadCount 계산 전체 멤버수 - 현재 채팅을 구독중인 수
		 */
		int chatId = chatMessage.getChatId();
		chatMessage.updateUnreadCnt(chatId, redisChatRoomRepo);
		/*
		 * db 에 저장한다.
		 */
		chatMessageService.insert(chatMessage);

		/*
		 * 마지막 구독한 메세지 업데이트한다.
		 */
		for (int i : redisChatRoomRepo.getChatMembers(chatId)) {
			chatmemberService.updateReadId(chatId, i, chatMessage.getId());
		}

		/*
		 * 소켓에 넣어둔다.
		 */
		ChatMessageVo chatMessageVo = modelMapper.map(chatMessage, ChatMessageVo.class);

		Map<String, Object> map = new HashMap<>();
		map.put("message", chatMessageVo);
		messaginTemplate.convertAndSend("/sub/chat/rooms/" + chatMessage.getChatId(), map);

		/*
		 * 현재 채팅방에 참여한 인원의 readTime을 갱신시킨다.
		 */
		redisChatRoomRepo.getChatMembers(chatId);
	}

}
