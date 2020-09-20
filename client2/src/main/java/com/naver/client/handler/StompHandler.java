package com.naver.client.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naver.client.common.JwtTokenProvider;
import com.naver.client.dto.ChatJoinDto;
import com.naver.client.redis.RedisChatRoomRepo;
import com.naver.client.service.ChatMemberService;
import com.naver.client.service.ChatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

	@Autowired
	private ChatService chatService;

	@Autowired
	private ChatMemberService chatMemberService;

	@Autowired
	private RedisChatRoomRepo redisChatRoomRepo;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private ObjectMapper objectMapper;
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		/*
		 * CONNECT
		 */
		if (StompCommand.CONNECT == accessor.getCommand()) {
			/*
			 * getToken
			 */
			String token = accessor.getFirstNativeHeader("Authorization");

			/*
			 * getUserId
			 */
			String userId = jwtTokenProvider.getUserNameFromJwt(token);
			log.info("The user is connected, userId : {}", userId);
			/*
			 * connect시 jwtToken을 이용해서 현재 접속중인 소켓의 id값을 저장한다.
			 */
			String sessionId = (String) message.getHeaders().get("simpSessionId");

			/*
			 * 현재 session에 대한 userId를 redis에저장
			 */
			redisChatRoomRepo.setSessionInfo(sessionId, userId);
		}
		/*
		 * SUBSCRIBE
		 */
		else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
			/*
			 * 채팅목록 sub인지 채팅 메시지 내 sub인지 구분해야한다. /sub/chat/rooms/{chatId}
			 * /sub/chat/list/{chatId}
			 */

			/*
			 * chatId를 가져온다.
			 */
			String chatId = chatService.getChatId((String) message.getHeaders().get("simpDestination"));
			if (!"Invalid".equals(chatId)) {
				log.info("The user is Subscribe, chat  chatId : {}", chatId);
				/*
				 * chatId의 총인원수를 redis server에 저장한다.
				 */
				redisChatRoomRepo.setChatCnt(chatId);
				/*
				 * 연결된 클라이언트를 식별할 수 있는 sessionId를 가져온다.
				 */
				String sessionId = (String) message.getHeaders().get("simpSessionId");

				/*
				 * 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다. (나중에 특정 세션이 어떤 채팅방에 있는지 알기 위해서)
				 */
				redisChatRoomRepo.setUserEnterInfo(sessionId, chatId);

				/*
				 * 채팅방의 인원수를 +1 한다.
				 */
				redisChatRoomRepo.plusUserCountInChat(chatId);

				/*
				 * 현재 sub하면 코넥션 했을때 유저아이디를 가져와서 현재 채팅방에서 내가 마지막으로 읽었던 messageId 와 지금 chat의 마지막
				 * messageId를 비교해서 그만큼 socket으로 갱신되었다고 뿌려주기 /sub/chat/unreadCnt/{chatId}
				 */

				int userId = redisChatRoomRepo.getSessionInfo(sessionId);
				// TODO 소켓 연결하기전에 이과정을 이미하므로 일단 주석처리
				// chatService.updateRealtimeUnreadCount(userId,Integer.parseInt(chatId));

				/*
				 * 현재 채팅방에 참석한 유저아이디를 넣는다.
				 */
				redisChatRoomRepo.setChatMembers(Integer.parseInt(chatId), userId);

			}
		}

		/*
		 * 소켓이 끊어진 경우
		 */
		else if (StompCommand.DISCONNECT == accessor.getCommand()) {
			/*
			 * 연결이 종료된 클라이언트 sessionId
			 */
			String sessionId = (String) message.getHeaders().get("simpSessionId");
			int userId = redisChatRoomRepo.getSessionInfo(sessionId);
			log.info("The User is Disconnect userId :{}", userId);

			/*
			 * 연결이 종료된 클라이언트의 chatId
			 */
			String chatId = redisChatRoomRepo.getUserEnterChatId(sessionId);

			/*
			 * chatId가 null 이 아니라면 sub/chat/room/{chatId}의 DISCONNECT이기 때문에 Hash에서 지워준다.
			 */
			if (chatId != null) {
				/*
				 * 나가기전에 이채팅방의 가장마지막글을 읽었다고 update해준다.
				 */
				chatMemberService.updateReadId(Integer.parseInt(chatId), userId);

				/*
				 * 채팅방의 인원수를 -1 한다.
				 */
				redisChatRoomRepo.minusUserCount(chatId);

				/*
				 * 채팅방에 세션아이디를 삭제한다.
				 */
				redisChatRoomRepo.removeUserEnterInfo(sessionId);

				/*
				 * 채팅방에 유저아이디를 삭제한다.
				 */
				redisChatRoomRepo.removeChatMembers(Integer.parseInt(chatId), userId);

				/*
				 * 유저아이디의 가장마지막 메세지를 저장한걸 삭제한다.
				 */
				redisChatRoomRepo.removeUserLastMessage(userId);
			}

			/*
			 * 저장한 session의 나의 아이디를 삭제한다.
			 */
			redisChatRoomRepo.removeSeesionInfo(sessionId);

			/*
			 * 끊어진 경우 해당하는게 맞으면 lastMessageId를 갱신한다.
			 */
		}

		/*
		 * 메세지를 보내는 경우
		 */
		else if (StompCommand.SEND == accessor.getCommand()) {
			/*
			 * 연결된 클라이언트의 세션정보로 아이디값을 가져온다.
			 */
			String sessionId = (String) message.getHeaders().get("simpSessionId");
			int userId = redisChatRoomRepo.getSessionInfo(sessionId);
			/*
			 * join OR message
			 */
			String destination = chatService.getDestination((String) message.getHeaders().get("simpDestination"));

			/*
			 * TODO join 처리 현재는 long-time 요청에대해서 순서가 뒤바뀔수잇음.
			 */
			if ("join".equals(destination)) {
				/*
				 * join에 대해서는 핸들러에서 직접 처리한다.
				 */
				if(message.getPayload() instanceof byte[]) {
					try {
						byte[] payload = (byte[])message.getPayload();
						ChatJoinDto chatjoinDto = objectMapper.readValue(payload, ChatJoinDto.class);
						chatService.chatJoin(chatjoinDto,userId);
					} catch (Exception e) {
					}
				}
				// TODO 지금은 리턴하고 pub/join이 직접 처리하고 있음.
			}
		}
		return ChannelInterceptor.super.preSend(message, channel);
	}
	
	

}
