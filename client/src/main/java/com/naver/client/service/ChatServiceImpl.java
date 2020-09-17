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

import com.naver.client.mapper.Chat;
import com.naver.client.mapper.ChatMessage;
import com.naver.client.mapper.LastMessageAndAt;
import com.naver.client.repo.ChatMemberRepo;
import com.naver.client.repo.ChatMessageRepo;
import com.naver.client.repo.ChatRepo;
import com.naver.client.repo.ChatUserRepo;
import com.naver.client.vo.ChatUserVo;
import com.naver.client.vo.ChatVo;
import com.naver.client.vo.MessageVo;
import com.naver.client.vo.UnReadCountVo;

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

	@Override
	public boolean delete(int id) {
		return chatRepo.delete(id);
	}

	@Override
	@Transactional
	public boolean insert(Chat chat, List<Integer> members, int myId) {
		/*
		 * members의 size == 1 : PRIVATE size >2 : GROUP
		 */
		if (members == null || members.size() == 0) {
			chat.setType("SELF");
		} else if (members.size() == 1) {
			chat.setType("PRIVATE");
		} else {
			chat.setType("GROUP");
		}
		/*
		 * 채팅 만드는시간 업데이트
		 */
		chat.update();

		// TODO 개인톡방을 만들기만하고 말을 서로안한 상태에서는 연결을 어떻게할지 생각
		chatRepo.insert(chat);
		int chatId = chat.getId();
		chatMemberRepo.insert(chatId, myId, 1);
		for (int userId : members) {
			chatMemberRepo.insert(chatId, userId, 0);
		}
		return true;
	}

	@Override
	public List<ChatUserVo> selectChatMembers(int chatId) {
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
			LastMessageAndAt lastMessageAndAt = chatMessageRepo.selectLastMessageAndAt(chat.getId());
			/*
			 * 읽지않은 수를 가져온다.
			 */
			int unreadCnt = chatMessageRepo.selectUnReadCount(chat.getId(), userId);
			/*
			 * 채팅방에 가입한 모든 멤버의 정보를 가져온다.
			 */
			List<ChatUserVo> members = chatUserRepo.selectChatMembers(chat.getId());
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
			chat.update(lastMessageAndAt, unreadCnt, members, chatRepo, chat.getId());

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
	public boolean updateRealtimeUnreadCount(int userId, int chatId) {

		/*
		 * 계산해서 만들엇음
		 */
		UnReadCountVo[] messages = chatMessageRepo.selectUnReadMessags(userId, chatId);

		chatMemberRepo.updateReadId(chatId, userId);

		/*
		 * webSocket으로 메세지 날리기
		 * 날릴게 없으면 안보낸다.
		 */
		if (messages.length != 0) {
			Map<String, Object> map = new HashMap<>();
			map.put("messages", messages);
			messaginTemplate.convertAndSend("/sub/chat/unreadCnt/" + chatId, map);
		}
		return true;
	}

	@Override
	public void outOfChat(int chatId, int userId) {
		// TODO unReadCnt 갱신
		UnReadCountVo[] messages = chatMessageRepo.selectUnReadMessags(userId, chatId);
		/*
		 * webSocket으로 메세지 날리기
		 * 날릴게 없으면 안보낸다.
		 */
		// TODO 나갓다는 메세지 보내기
		String name = chatUserRepo.selectOneName(userId);
		
		/*
		 * 채팅방에 메세지 전송
		 */
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setChatId(chatId);
		chatMessage.setContent(name+"님이 나갔습니다");
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
			map = new HashMap<>();
			map.put("messages", messages);
			messaginTemplate.convertAndSend("/sub/chat/unreadCnt/" + chatId, map);
		}

	}

}
