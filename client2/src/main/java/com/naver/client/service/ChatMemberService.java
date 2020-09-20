package com.naver.client.service;

import java.util.List;

import com.naver.client.mapper.ChatMember;
import com.naver.client.vo.ChatPrivateMemberVo;

public interface ChatMemberService {
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
	 * 1:1 채팅방이면 삭제가아니라 비활성화로 바꾼다.
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
	 * 채팅방에 있는 모든 멤버를 조회한다.
	 */
	int selectChatMemebersCnt(String chatId);
	
	/*
	 * 채팅의 참여한 유저의 가장 최근에 읽은 chatmessageId를 업데이트한다.
	 * (가장 나중에 말한 값으로)
	 */
	boolean updateReadId(int chatId, int userId);

	boolean updateReadId(int chatId, int userId, int lastreadId);
	
	/*
	 * 내아이디와 채팅방의 정보로 개인정보로 상대방이 채팅방 활성화 상태인지 확인
	 */
	ChatPrivateMemberVo selectPrivateChatMemberView(int userId, int chatId);
		
	/*
	 * chatMember join시간 업데이트및 참가하였으니 상대방을 초대한다.
	 */
	boolean updateJoin(ChatMember chatMember);
	
	/*
	 * userId chatId에 해당하는 jointime을 가져온다.
	 */
	long selectOneJointime(int userId, int chatId);
	
	
	/*
	 * 채팅에 참여한 모든 userId들을 가져온다.
	 */
	int[] selectChatMemberUserIds(int chatId);
	
}
