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
import com.naver.client.resource.BaseResource;
import com.naver.client.resource.CommonResource;
import com.naver.client.vo.ChatUserVo;
import com.naver.client.vo.ChatVo;

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
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		/*
		 * Mapping
		 */
		Chat chat = modelMapper.map(chatDto, Chat.class);
		List<Integer> members = new ArrayList<Integer>(chatDto.getFriendIds());
		
		if(members==null || members.get(0)==null)
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
		// TODO token 검증 작업이 필요하다.
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		List<ChatVo> chats = chatService.selectChatList(userId);

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

		Map<String, Object> map = new HashMap<>();
		List<Integer> successes = new ArrayList<>();

		// TODO token 검증 작업 필요하다.

		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		for (int chatId : chatDeleteDto.getChatIds()) {
			if (chatMemberService.delete(chatId, userId)) {
				successes.add(chatId);
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

		/*
		 * TODO socket에 메세지를 보내고 검증한다
		 */

		return ResponseEntity.ok(new CommonResource(OK_CODE, OK, map));

	}

	/*
	 * 채팅 내 멤버 조회
	 */
	@GetMapping("/{chatId}/members")
	public ResponseEntity searchChatMember(@RequestHeader("Authorization") String token,
			@PathVariable("chatId") int chatId) {
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		List<ChatUserVo> chatUserVos = chatService.selectChatMembers(chatId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("members", chatUserVos);

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
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));

		Map<String, Object> map = new HashMap<String, Object>();

		if ("BASIC".equals(lastMessageId)) {
			/*
			 * 읽음 처리후 소켓에 연결중인 유저들에게 읽음 카운트처리
			 */
			chatService.updateRealtimeUnreadCount(userId, chatId);
			map.put("messages", chatMessageService.selectMessageVoNoOption(chatId, Integer.parseInt(size)));
		} else {
			map.put("messages", chatMessageService.selectMessageVoLastMessageIdOption(chatId, Integer.parseInt(size),
					Integer.parseInt(lastMessageId)));
		}

		return ResponseEntity.ok(new CommonResource(OK_CODE, OK, map));
	}

	/*
	 * 채팅방 읽음 처리
	 */
	@PutMapping("/{chatId}/unreadCnt")
	public ResponseEntity readChatMessage(@RequestHeader("Authorization") String token,@PathVariable("chatId") int chatId) {
		int userId = Integer.parseInt(jwtTokenProvider.getUserNameFromJwt(token));
		/*
		 * chatId의 마지막 메세지의 번호로 chatMember 의 lastreadId를 업데이트 한다.
		 */
		chatService.updateRealtimeUnreadCount(userId, chatId);
		
		return ResponseEntity.ok(new BaseResource(OK_CODE, OK));
	}
}
