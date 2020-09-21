package com.naver.client.apicontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.naver.client.common.BaseController;
import com.naver.client.dto.ChatDeleteDto;
import com.naver.client.dto.ChatDto;
import com.naver.client.mapper.Chat;
import com.naver.client.mapper.ChatMember;
import com.naver.client.mapper.MessageSearch;
import com.naver.client.resource.BaseResource;
import com.naver.client.resource.CommonResource;
import com.naver.client.vo.ChatMemberVo;
import com.naver.client.vo.ChatMessageVo;
import com.naver.client.vo.ChatVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chats")
public class ChatApiController extends BaseController {

	/*
	 * chat 만들기
	 */
	@PostMapping
	public ResponseEntity createChat(@RequestBody ChatDto chatDto, @RequestHeader("Authorization") String token) {
		// TODO chatUserId는 @header의 access_token을 해석해서 사용한다.

		log.info("createChat");

		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		/*
		 * Mapping
		 */
		Chat chat = modelMapper.map(chatDto, Chat.class);
		List<Integer> members = new ArrayList<Integer>(chatDto.getFriendIds());

		if (members == null || members.get(0) == null)
			members = new ArrayList<>();

		/*
		 * inset
		 */
		chatService.insert(chat, members, userId);
		/*
		 * createVo
		 */
		ChatVo chatVo = modelMapper.map(chat, ChatVo.class);
		members.add(userId);
		chatVo.setMembers(chatUserService.selectIds(members));
		chatVo.setLastAt(chat.getCreatetime());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("chat", chatVo);

		return ResponseEntity.ok(new CommonResource(OK_CODE, OK, map));
	}

	/*
	 * userId가 참여하는 chat목록을 반환한다.
	 */
	@GetMapping
	public ResponseEntity searchChatList(@RequestHeader("Authorization") String token) {
		log.info("searchChatList");

		// TODO token 검증 작업이 필요하다.
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));
		log.info("jwtTokenProvider userId : {}",userId);
		List<ChatVo> chats = chatService.selectChatList(userId);
		log.info("chatService complete");
		HashMap<String, Object> map = new HashMap<>();
		map.put("chats", chats);

		return ResponseEntity.ok().body(new CommonResource(OK_CODE, OK, map));

	}

	/*
	 * 채팅방 나가기
	 */
	@PostMapping("/delete")
	public ResponseEntity deleteChat(@RequestHeader("Authorization") String token,
			@RequestBody ChatDeleteDto chatDeleteDto) {
		log.info("deleteChat");
		Map<String, Object> map = new HashMap<>();
		List<Integer> successes = new ArrayList<>();

		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		for (int chatId : chatDeleteDto.getChatIds()) {

			if ("PRIVATE".equals(chatService.selectType(chatId))) {
				chatMemberService.updateView(new ChatMember(chatId, userId, false));
				successes.add(chatId);
			} else {
				if (chatMemberService.delete(chatId, userId)) {
					successes.add(chatId);
					chatService.outOfChat(chatId, userId);
					log.info("setChatCnt");
					redisChatRoomRepo.setChatCnt(chatId);
				}
				/*
				 * 성공적으로 삭제햇으면 소켓으로 나갔다는 메세지를 날리고 unReadCnt를 계산한값을 다시 보낸다.
				 */

			}
		}

		/*
		 * 삭제 성공한 곳은 나가기 메세지를 소켓과 DB에 저장한다.
		 */
		int[] ret = new int[successes.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = successes.get(i).intValue();
		}
		map.put("chatIds", ret);

		return ResponseEntity.ok(new CommonResource(OK_CODE, OK, map));

	}

	/*
	 * 채팅 내 멤버 조회
	 */
	@GetMapping("/{chatId}/members")
	public ResponseEntity searchChatMember(@RequestHeader("Authorization") String token,
			@PathVariable("chatId") int chatId) {
		log.info("searchChatMember");
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		List<ChatMemberVo> members = chatService.selectChatMembers(chatId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("members", members);

		return ResponseEntity.ok(new CommonResource(OK_CODE, OK, map));

	}

	/*
	 * 채팅 메시지 조회
	 */
	@GetMapping("/{chatId}")
	public ResponseEntity searchMessages(@RequestHeader("Authorization") String token,
			@RequestParam(value = "lastMessageId", required = false, defaultValue = "BASIC") String lastMessageId,
			@RequestParam(value = "size", required = false, defaultValue = "20") String size,
			@PathVariable("chatId") int chatId) {

		log.info("searchMessages");
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		Map<String, Object> map = new HashMap<String, Object>();
		List<ChatMessageVo> messages = null;
		if ("BASIC".equals(lastMessageId)) {
			log.info("BASIC");
			/*
			 * 읽음 처리후 소켓에 연결중인 유저들에게 읽음 카운트처리
			 */
			chatService.updateRealtimeUnreadCount(userId, chatId);
			log.info("BASIC message success");
			long jointime = chatMemberService.selectOneJointime(userId,chatId);
			MessageSearch messageSearch = new MessageSearch();
			messageSearch.setChatId(chatId);
			messageSearch.setSize(Integer.parseInt(size));
			messageSearch.setJointime(jointime);
			messages = chatMessageService.selectMessageVoNoOption(messageSearch);
		} else {
			log.info("OPTIONAL");
			long jointime = chatMemberService.selectOneJointime(userId,chatId);
			MessageSearch messageSearch = new MessageSearch();
			messageSearch.setChatId(chatId);
			messageSearch.setSize(Integer.parseInt(size));
			messageSearch.setJointime(jointime);
			messageSearch.setLastMessageId(Integer.parseInt(lastMessageId));
			messages = chatMessageService.selectMessageVoLastMessageIdOption(messageSearch);
		}
		
		map.put("messages", messages);

		/*
		 * 현재 소켓의 아이디가 보고있는 메세지 페이지가 어디인지 저장한다. 이 값을 이용해서 유저마다 필요한 unreadCnt만 갱신해준다.
		 */
		if (messages.size() != 0) {
			redisChatRoomRepo.setUserLastMessage(userId, messages.get(0).getId());

		}
		return ResponseEntity.ok(new CommonResource(OK_CODE, OK, map));
	}

	/*
	 * 채팅방 읽음 처리
	 */
	@PutMapping("/{chatId}/unreadCnt")
	public ResponseEntity readChatMessage(@RequestHeader("Authorization") String token,
			@PathVariable("chatId") int chatId) {
		log.info("readChatMessage");
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));
		/*
		 * chatId의 마지막 메세지의 번호로 chatMember 의 lastreadId를 업데이트 한다.
		 */
		chatService.updateRealtimeUnreadCount(userId, chatId);

		return ResponseEntity.ok(new BaseResource(OK_CODE, OK));
	}
}
