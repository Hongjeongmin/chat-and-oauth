package com.naver.client.repo;

import java.util.List;

import com.naver.client.mapper.ChatMember;
import com.naver.client.vo.ChatPrivateMemberVo;

public interface ChatMemberRepo {
	/*
	 * insert chatMember table
	 */
	boolean insert(ChatMember chatMember);
	
	/*
	 * update chatMember view
	 */
	boolean updateView(ChatMember chatMember);
	/*
	 * delete  chatMember table
	 */
	boolean delete(int chatId,int userId);
	
	/*
	 * 가장 최신으로 읽은 메시지의 시간으로 update
	 */
	boolean updateReadTime(ChatMember chatMember);
	
	/*
	 * chat.userId 로 참여한 모든 채팅방 조회
	 */
	List<Integer> selectChatUserIds(int userId);
	/*
	 * chat.chatId 로 참여한 모든 멤버 조회
	 */
	List<Integer> selectChatMemeberIds(int chatId);
	
	/*
	 * 채팅방에 있는 모든 멤버의 수 조회
	 */
	int selectChatMemebersCnt(String chatId);
	
	/*
	 * 채팅방의 유저가 가장 마지막에 읽은 메시지 아이디를 업데이트 한다.
	 * 
	 */
	boolean updateReadId(int chatId, int userId);

	boolean updateReadIdWithValue(int chatId, int userId, int lastreadId);
	
	/*
	 * 내아이디와 채팅방의 아이디정보로 상대방이 현재 채팅방이 활성화되어있는 상태인지 확
	 */
	ChatPrivateMemberVo selectPrivateChatMemberView(int userId, int chatId);
	
	/*
	 * join시 view를 활성화시키고 데이트시킨다.
	 */
	boolean updateJoin(ChatMember chatMember);
	
	/*
	 * jointime 조회
	 */
	long selectOneJointime(int userId, int chatId);
	
	/*
	 * chatId에 참여한 모든 userId를 반환한다.
	 */
	int[] selectChatMemberUserIds(int chatId);
	

}
