package com.naver.client.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naver.client.mapper.Chat;
import com.naver.client.mapper.LastMessageAndAt;
import com.naver.client.repo.ChatMemberRepo;
import com.naver.client.repo.ChatMessageRepo;
import com.naver.client.repo.ChatRepo;
import com.naver.client.repo.ChatUserRepo;
import com.naver.client.vo.ChatUserVo;
import com.naver.client.vo.ChatVo;

@Service
public class ChatServiceImpl implements ChatService {
	@Autowired
	ChatRepo chatRepo;

	@Autowired
	ChatMemberRepo chatMemberRepo;

	@Autowired
	ChatMessageRepo chatMessageRepo;

	@Autowired
	ChatUserRepo chatUserRepo;

	@Autowired
	ModelMapper modelMapper;

	@Override
	public boolean delete(int id) {
		return chatRepo.delete(id);
	}

	@Override
	@Transactional
	public boolean insert(Chat chat, List<Integer> members) {
		/*
		 * members의 size == 2 : PRIVATE size >2 : GROUP
		 */
		System.out.println(members.size());
		if (members.size() == 2) {
			chat.setType("PRIVATE");
		}else {
			chat.setType("GROUP");
		}
		chatRepo.insert(chat);
		int chatId = chat.getId();
		for (int userId : members) {
			chatMemberRepo.insert(chatId, userId);
		}

		return true;
	}

//
//	@Override
//	public List<ChatVo> selectChatListTest(int userId) {
//
//		/*
//		 * init
//		 */
//		List<ChatVo> chats = new ArrayList<ChatVo>();
//
//		/*
//		 * userId로 참여한 모든 채팅방 목록 조회
//		 */
//		List<Integer> chatIds = chatMemberRepo.selectChatUserIds(userId);
//
//		/*
//		 * 각각의 chatId를 조회힌다.
//		 */
//
//		/*
//		 * members에는 나에대한 정보는 고정. 따라서 sql로 조회할 필요가 없다.
//		 */
////		ChatUserVo myVo = chatUserRepo.selectOneVo(userId);
//
//		for (int chatId : chatIds) {
//
//			/*
//			 * chatId를 이용해서 chat값 반환.
//			 */
//			Chat chat = chatRepo.selectOne(chatId);
//
//			/*
//			 * 채팅방의 lastMessage 조회
//			 */
//			LastMessageAndAt lastMessageAndAt = chatMessageRepo.selectLastMessageAndAt(chatId);
//
//			/*
//			 * 읽지 않은 수 count
//			 */
//			int unreadCnt = chatMessageRepo.selectUnReadCount(chatId, userId);
//
//			/*
//			 * test 1 나를 포함한 채팅방 참여자들 조회 3400ms
//			 */
//			List<ChatUserVo> members = chatUserRepo.selectChatMembers(chatId);
//
//			/*
//			 * test 2 나를 포함하지 않은 채팅밤 참여자들 조회 3400ms
//			 */
////			List<ChatUserVo> members = chatUserRepo.selectChatFriends(chatId, userId);
////			members.add(myVo);
//
//			/*
//			 * chatVo 생성
//			 */
//			ChatVo chatVo = new ChatVo(chat, lastMessageAndAt, unreadCnt, members);
//
//			chats.add(chatVo);
//		}
//
//		return chats;
//	}

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

			chat.update(lastMessageAndAt, unreadCnt, members);
		}

		return chats;
	}

}
