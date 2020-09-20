package com.naver.client.service;

import java.util.List;

import com.naver.client.dto.ChatJoinDto;
import com.naver.client.mapper.Chat;
import com.naver.client.vo.ChatMemberVo;
import com.naver.client.vo.ChatVo;
import com.naver.client.vo.UpdateChatVo;

public interface ChatService {
	/*
	 * chat 생성 
	 * chat table생성 후 chat 테이블의 참여자 members 생성
	 */
	public boolean insert(Chat chat, List<Integer> members,int myId);
	
	/*
	 * chat 삭제
	 */
	public boolean delete(int id);
	

	
	/*
	 * 채팅방에 모든 멤버들을 조회해서 반환한다.
	 */
	List<ChatMemberVo> selectChatMembers(int chatId);
	
	/*
	 * 채팅 목록 조회
	 * userId 값으로 참여한 모든 채팅목록의 정보를 반환한다.
	 * sendAt순으로 정렬하기
	 */
	
	List<ChatVo> selectChatList(int userId);
	
	/*
	 * destination 정보에서 chatId 추출하기
	 */
	public String getChatId(String destination);
	
	/*
	 * destination 정보에서 join or message 추출하기
	 */
	public String getDestination(String destination);

	
	/*
	 * userId가 최근에 읽은 채팅메세지와 현재 채팅메세지를 비교하여 차이가 있으면 그만큼
	 * 소켓에 현재구독하고있는 사람들에게 읽었다고 날려주고 unreadCnt를 업데이트해준다.
	 */
	public boolean updateRealtimeUnreadCount(int userId, int chatId);
	
	
	/*
	 * 소켓에 나갔다는 메세지를 보내고 unReadCnt를 갱신한다.
	 */
	public void outOfChat(int chatId,int userId);
	
	/*
	 * 메세지 보내는 기
	 */
	public void snedMessage(ChatJoinDto chatJoinDto,int userId);
	
	/*
	 * selectType 반환
	 */
	public String selectType(int chatId);

	/*
	 * joinMessage 초대 , 초대메세지 전송, members활성화 unReadCnt 갱신
	 */
	public void chatJoin(ChatJoinDto chatjoinDto, int userId);
	
	/*
	 * 새로운 방생성에대한 소켓 전송
	 */
	public void newChatList(int userId,ChatVo chatVo);
	
	/*
	 * 갱신에 대한 소켓 전송
	 */
	public void existChatList(int userId, UpdateChatVo updateChatVo);
}
