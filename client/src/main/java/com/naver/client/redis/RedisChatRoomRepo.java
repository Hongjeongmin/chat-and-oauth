package com.naver.client.redis;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.naver.client.service.ChatMemberService;
import com.naver.client.service.ChatService;

import lombok.RequiredArgsConstructor;

@Service
public class RedisChatRoomRepo {

	/*
	 * CashKeys
	 */
	private static final String SESSION_INFO = "SESSION_INFO"; // 특정 세션이 어떤 유저인지 저장
	private static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
	private static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId
	private static final String CHAT_CNT = "CHAT_CNT"; // 채팅방 총인원 매번 입장할때마다 바뀔수 있으니 확인
	private static final String CHAT_ENTER_MEMBER = "CHAT_ENTER_MEMBER";// 채팅방에 입장한 userID들

	/*
	 * Service
	 */
	@Autowired
	private ChatService chatService;
	@Autowired
	private ChatMemberService chatMemberService;

	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> valueOpsSessionInfo;
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOpsEnterInfo;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valueOpsUserCount;
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOpsChatCnt;
	@Resource(name = "redisTemplate")
	private HashOperations<String, Integer, Integer> hashOpsChatEnterMember;

	/*
	 * 세션이 어떤 아이디인지 저장
	 */
	public void setSessionInfo(String sessionId, String userId) {
		valueOpsSessionInfo.put(SESSION_INFO, sessionId, userId);
	}
	
	/*
	 * 세션이 어떤 아디인지 조회
	 */
	public int getSessionInfo(String sessionId) {
		return Integer.parseInt(valueOpsSessionInfo.get(SESSION_INFO, sessionId));
	}
	
	/*
	 * 세션이 어떤 아이딘지 삭제
	 */
	public void removeSeesionInfo(String sessionId) {
		valueOpsSessionInfo.delete(SESSION_INFO, sessionId);
	}

	/*
	 * 채팅방 총인원 저장
	 */
	public void setChatCnt(String chatId) {
		hashOpsChatCnt.put(CHAT_CNT, chatId, chatMemberService.selectChatMemebersCnt(chatId) + "");
	}

	/*
	 * 채팅방 총인원 조회
	 */
	public int getChatCnt(int chatId) {
		return Integer.valueOf(hashOpsChatCnt.get(CHAT_CNT, chatId + ""));
	}

	/*
	 * 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
	 */
	public void setUserEnterInfo(String sessionId, String chatId) {
		hashOpsEnterInfo.put(ENTER_INFO, sessionId, chatId);
	}

	/*
	 * 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 삭제
	 */
	public void removeUserEnterInfo(String sessionId) {
		hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
	}

	/*
	 * 유저 Session으로 입장한 chatId 조회
	 */
	public String getUserEnterChatId(String sessionId) {
		return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
	}

	/*
	 * 채팅방 유저수 조회 없으면 0반환.
	 */
	public int getUserCountInChat(String chatId) {
		return Integer.valueOf(Optional.ofNullable(valueOpsUserCount.get(USER_COUNT + "_" + chatId)).orElse("0"));
	}

	public int getUserCountInChat(int chatId) {
		return Integer.valueOf(Optional.ofNullable(valueOpsUserCount.get(USER_COUNT + "_" + chatId)).orElse("0"));
	}

	/*
	 * 채팅방 입장한 유저수 +1
	 */
	public long plusUserCountInChat(String chatId) {
		return Optional.ofNullable(valueOpsUserCount.increment(USER_COUNT + "_" + chatId)).orElse(0L);
	}

	/*
	 * 채팅방 입장한 유저수 -1
	 */
	public long minusUserCount(String chatId) {
		return Optional.ofNullable(valueOpsUserCount.decrement(USER_COUNT + "_" + chatId)).filter(count -> count > 0)
				.orElse(0L);
	}

	/*
	 * 채팅방에 있는 모든 멤버의 userId를 반환한다.
	 */
	public List<Integer> getChatMembers(int chatId) {
		return hashOpsChatEnterMember.values(CHAT_ENTER_MEMBER+chatId);
	}

	/*
	 * 현재 유저채팅방에 입장
	 */
	public void setChatMembers(int chatId, int userId) {
		hashOpsChatEnterMember.put(CHAT_ENTER_MEMBER+chatId, userId, userId);
	}
	
	/*
	 * 현재 채팅방 맵핑 정보 삭제
	 */
	public void removeChatMembers(int chatId,int userId) {
		hashOpsChatEnterMember.delete(CHAT_ENTER_MEMBER+chatId, userId);
	}
	
	/*
	 * 현재 채팅방에 소켓으로 접속해잇는 모든멤버 반환
	 */
	public List<Integer> findAllEnterMemberInChat(int chatId){
		return hashOpsChatEnterMember.values(CHAT_ENTER_MEMBER+chatId);
	}
	
	
}
