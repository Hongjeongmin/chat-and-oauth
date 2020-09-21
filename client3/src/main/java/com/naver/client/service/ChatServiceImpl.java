package com.naver.client.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naver.client.dto.ChatJoinDto;
import com.naver.client.mapper.Chat;
import com.naver.client.mapper.ChatMember;
import com.naver.client.mapper.ChatMessage;
import com.naver.client.mapper.LastMessageAndAt;
import com.naver.client.redis.RedisChatRoomRepo;
import com.naver.client.repo.ChatMemberRepo;
import com.naver.client.repo.ChatMessageRepo;
import com.naver.client.repo.ChatRepo;
import com.naver.client.repo.ChatUserRepo;
import com.naver.client.resource.UpdateResource;
import com.naver.client.vo.ChatMemberVo;
import com.naver.client.vo.ChatMessageVo;
import com.naver.client.vo.ChatVo;
import com.naver.client.vo.MessageVo;
import com.naver.client.vo.UnReadCountVo;
import com.naver.client.vo.UpdateChatVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {
	@Autowired
	private ChatRepo chatRepo;

	@Autowired
	private ChatMemberRepo chatMemberRepo;

	@Autowired
	private ChatMessageRepo chatMessageRepo;

	@Autowired
	private ChatUserRepo chatUserRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private SimpMessageSendingOperations messaginTemplate;

	@Autowired
	private ChatMessageService chatMessageService;

	@Autowired
	private RedisChatRoomRepo redisChatRoomRepo;
	
	@Autowired
	private ChatUserService chatUserService;
	
	@Autowired
	private ChatMemberService chatMemberService;

	@Override
	public boolean delete(int id) {
		return chatRepo.delete(id);
	}

	@Override
	@Transactional
	public boolean insert(Chat chat, List<Integer> members, int myId) {
		/*
		 * SELF 메세지 (자기자신 대화)
		 */
		if (members == null || members.size() == 0) {
			chat.setType("SELF");
			chat.update();
			chatRepo.insert(chat);
			int chatId = chat.getId();
			chatMemberRepo.insert(new ChatMember(chatId, myId, true));
		}
		/*
		 * PRIVATE ( 1:1대화)
		 * 
		 * 1. 기존에 그사람과의 채팅방이 존재하는지 확인한다. 2. 없다면 새로만들고 상대를 비활성화 상태로만든다. 3. 존재한다면 활성화 한다.
		 */
		else if (members.size() == 1) {
			chat.setType("PRIVATE");

			// 1. 기존에 채팅방이 존재하는지 확인한다.
			// 있으면 채팅방번호 반환한다.
			Integer tmp = chatRepo.isExist(myId, members.get(0));

			// 없으면 새로만든다.
			if (tmp == null) {
				chat.update();
				chatRepo.insert(chat);
				int chatId = chat.getId();
				chatMemberRepo.insert(new ChatMember(chatId, myId, true));
				chatMemberRepo.insert(new ChatMember(chatId, members.get(0), false));
				

			}
			// 잇으면 활성화 시켜준다.
			else {
				ChatMember chatMember = new ChatMember(tmp, myId, true);
				chatMemberRepo.updateView(chatMember);
				/*
				 * id 값 lastAt값을 활성화한다.
				 */
				chat.setId(tmp);
				chat.setCreatetime(chatMember.getJointime());
			}
			
		}
		/*
		 * GROP대화
		 */
		else {
			chat.setType("GROUP");
			chat.update();
			chatRepo.insert(chat);
			int chatId = chat.getId();
			// 방만든 사람만 활성화
			chatMemberRepo.insert(new ChatMember(chatId, myId, true));

			// 나머지는 비활성화
			for (int userId : members) {
				chatMemberRepo.insert(new ChatMember(chatId, userId, false));
			}
		}

		/*
		 * 채팅 만드는시간 업데이트
		 */

		return true;
	}

	@Override
	public List<ChatMemberVo> selectChatMembers(int chatId) {
		return chatUserRepo.selectChatMembers(chatId);
	}

	@Override
	public List<ChatVo> selectChatList(int userId) {
		/*
		 * userId가 가입한 모든 채팅방의 기본 정보를 받아온다.
		 */
		List<ChatVo> chats = chatRepo.selectAllChatAsUserId(userId);

		for (ChatVo chat : chats) {
			/*
			 * lastMessage 와 sentAt을 가져온다.
			 */
			LastMessageAndAt lastMessageAndAt = chatMessageRepo.selectLastMessageAndAt(chat.getId(),userId);
			/*
			 * 읽지않은 수를 가져온다.
			 */
			int unreadCnt = chatMessageRepo.selectUnReadCount(chat.getId(), userId);
			/*
			 * 채팅방에 가입한 모든 멤버의 정보를 가져온다.
			 */
			List<ChatMemberVo> members = chatUserRepo.selectChatMembers(chat.getId());
			/*
			 * 개인 채팅일 경우 null 을 넣는다.
			 */
			if ("PRIVATE".equals(chat.getType())) {
				chat.setImage(null);
				chat.setName(null);
			}

			/*
			 * 라스트 메세지와 시간 업데이트한다. 만약 라트스메세지가 없을경우 lastAt에 채팅방이 만들어진 시간을 set한다.
			 */
			chat.update(lastMessageAndAt, unreadCnt, members, chatMemberRepo, chat.getId(),userId);

		}

		/*
		 * lastAt 순으로 오름차순 정렬
		 */
		Collections.sort(chats);

		return chats;
	}

	@Override
	public String getChatId(String destination) {
		int lastIndex = destination.lastIndexOf('/');
		if ("/sub/chat/rooms".equals(destination.substring(0, lastIndex))) {
			if (lastIndex != -1)
				return destination.substring(lastIndex + 1);
		}
		return "Invalid";
	}

	@Override
	public String getDestination(String destination) {
		int lastIndex = destination.lastIndexOf('/');
		if (lastIndex != -1) {
			return destination.substring(lastIndex + 1);
		}
		return "Invalid";
	}

	@Override
	public boolean updateRealtimeUnreadCount(int userId, int chatId) {

		/*
		 * 계산해서 만들엇음
		 */
		UnReadCountVo[] messages = chatMessageRepo.selectUnReadMessags(userId, chatId);

		log.info("updateReadId preSend");
		chatMemberRepo.updateReadId(chatId, userId);
		log.info("updateReadId afterSend");

		/*
		 * webSocket으로 메세지 날리기 날릴게 없으면 안보낸다.
		 */
		if (messages.length != 0) {
			sendUnreadCntMessage(messages, chatId);
		}
		log.info("updateReadId success");

		return true;
	}

	@Override
	public void outOfChat(int chatId, int userId) {
		// TODO unReadCnt 갱신
		UnReadCountVo[] messages = chatMessageRepo.selectUnReadMessags(userId, chatId);
		/*
		 * webSocket으로 메세지 날리기 날릴게 없으면 안보낸다.
		 */
		// TODO 나갓다는 메세지 보내기
		String name = chatUserRepo.selectOneName(userId);

		/*
		 * 채팅방에 메세지 전송
		 */
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setChatId(chatId);
		chatMessage.setContent(name + "님이 나갔습니다");
		chatMessage.setType("NOTI");

		/*
		 * DB에 저장
		 */
		chatMessageService.insert(chatMessage);

		Map<String, Object> map = new HashMap<>();
		map.put("message", modelMapper.map(chatMessage, MessageVo.class));
		messaginTemplate.convertAndSend("/sub/chat/rooms/" + chatId, map);

		/*
		 * 갱신된 unReadCnt 보내기
		 */
		if (messages.length != 0) {
			sendUnreadCntMessage(messages, chatId);
		}

	}

	@Override
	public void snedMessage(ChatJoinDto chatJoinDto,int userID) {
		System.out.println(chatJoinDto.getChatId());
	}

	@Override
	public String selectType(int chatId) {
		return chatRepo.selectType(chatId);
	}
	
	
	//수정이필요하다
	void sendUnreadCntMessage(UnReadCountVo[] messages, int chatId) {
		for (int id : redisChatRoomRepo.getChatMembers(chatId)) {
			/*
			 * 지금 소켓의 참가인원이 조회하고있는 채팅목록만큼만 갱신해라고 보내준다.
			 * 지금은 그냥 다보내는 수준.
			 */
			
			//TODO 지금 소켓은user가 읽고있는 메세지 아이디이다.
//			redisChatRoomRepo.getUserLastMessage(id);
			
			Map<String, Object> map = new HashMap<>();
			map.put("messages", messages);
			map.put("type", "UNREADCNT");
			messaginTemplate.convertAndSend("/sub/chat/unreadCnt/" + chatId + "/" + id, map);
		}
	}

	@Override
	public void chatJoin(ChatJoinDto chatJoinDto, int userId) {
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
		 * Chat Member 활성화
		 */
		for (int m : chatJoinDto.getInvitedIds()) {
			chatMemberService.updateView(new ChatMember(chatJoinDto.getChatId(), m, true));
		}
		
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
		map.put("message", modelMapper.map(chatMessage, ChatMessageVo.class));
		messaginTemplate.convertAndSend("/sub/chat/rooms/" + chatJoinDto.getChatId(), map);
		Chat chat = chatRepo.selectOne(chatJoinDto.getChatId());
		ChatVo chatVo = new ChatVo();
		chatVo.setName(chat.getName());
		chatVo.setImage(chat.getImage());
		chatVo.setId(chatJoinDto.getChatId());
		chatVo.setLastAt(chatMessage.getSentAt());
		chatVo.setType("GROUP");
		chatVo.setUnreadCnt(1);
		List<ChatMemberVo> members = chatUserService.selectChatMembers(chatJoinDto.getChatId());
		chatVo.setMembers(members);
		for(int m :chatJoinDto.getInvitedIds())
			newChatList(m, chatVo);
	}

	@Override
	public void newChatList(int userId,ChatVo chatVo) {
//		messaginTemplate.convertAndSend("/sub/chat/list/" + userId, new UpdateResource("NEW", chatVo));
		Map<String, Object> map = new HashMap<>();
		map.put("type", "NEW");
		map.put("chat", chatVo);
		messaginTemplate.convertAndSend("/sub/chat/list/" + userId, map);
	}

	@Override
	public void existChatList(int userId,UpdateChatVo updateChatVo) {
		log.info("existChatList : /sub/chat/list/{}",userId);
		Map<String, Object> map = new HashMap<>();
		map.put("type", "EXIST");
		map.put("chat", updateChatVo);
		messaginTemplate.convertAndSend("/sub/chat/list/" + userId, map);
	}

}
