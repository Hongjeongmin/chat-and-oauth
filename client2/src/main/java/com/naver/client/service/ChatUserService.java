package com.naver.client.service;

import java.util.List;

import com.naver.client.mapper.ChatUser;
import com.naver.client.vo.ChatMemberVo;
import com.naver.client.vo.ChatUserVo;

public interface ChatUserService {
	
	/*
	 * ChatUser 생성
	 */
	boolean insert(ChatUser chatUser);
	
	/*
	 * id로 name 조회
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
	 * 전체사용자 조회
	 */
	List<ChatUserVo> selectAllUserVos();
	
	/*
	 * id값으로 조회 return ChatUserVo 형태
	 */
	ChatUserVo selectOneVo(int id);
	
	/*
	 * ids에 있는 값을 조회해서 List<ChatUserVo> 형태로 반환 
	 */
	List<ChatMemberVo> selectIds(List<Integer> ids);
	
	/*
	 * username으로 id 조회
	 */
	int selectIdAsUsername(String username);
	
	
	/*
	 * user를 활성화시킨다.
	 */
	List<String> selectInvitedUsers(int[] invitedIds);
	
	/*
	 * chat에참가한 모든 members의 정보를 가져온다.
	 */
	List<ChatMemberVo> selectChatMembers(int chatId);
}
