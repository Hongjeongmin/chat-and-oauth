package com.naver.client.repo;

import java.util.List;

import com.naver.client.mapper.ChatUser;
import com.naver.client.vo.ChatMemberVo;
import com.naver.client.vo.ChatUserVo;

public interface ChatUserRepo {
	
	/*
	 * ChatUser 생성
	 */
	boolean insert(ChatUser chatUser);
	
	/*
	 *  id 로 name 조회
	 */
	String selectOneName(int id);
	
	/*
	 *  id 로 이미지 수정
	 */
	boolean updateImage(ChatUser chatUser);
	
	/*
	 * id로 ChatUser 조회
	 */
	ChatUser selectOne(int id);
	
	/*
	 * id로 친구 조회
	 */
	List<ChatUserVo> selectFriendVos(int id);
	
	/*
	 * id값을 포함하고 모두조회
	 */
	List<ChatUserVo> selectAllUserVos();
	
	/*
	 * id값으로 조회 return ChatUserVo 형태
	 */
	ChatUserVo selectOneVo(int id);
	
	
	/*
	 * 채팅방 참여인원 조회 나를 제외하고 반환한다
	 */
	List<ChatUserVo> selectChatFriends(int chatId, int userId);

	
	/*
	 * 채팅방 참여인원 조회 나를 포함하고 반환한다.
	 */
	List<ChatMemberVo> selectChatMembers(int chatId);
	
	/*
	 * username으로 id 조회
	 */
	int selectIdAsUsername(String username);
}
