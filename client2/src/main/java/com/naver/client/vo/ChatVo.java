package com.naver.client.vo;

import java.util.List;

import com.naver.client.mapper.Chat;
import com.naver.client.mapper.LastMessageAndAt;
import com.naver.client.repo.ChatRepo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatVo implements Comparable<ChatVo>{
	int id;
	String image;
	String name;
	String lastMessage;
	Long lastAt;
	String type;
	int unreadCnt;
	List<ChatUserVo> members;

	public ChatVo(Chat chat, LastMessageAndAt lastMessageAndAt, int unreadCnt, List<ChatUserVo> members) {

		this.id = chat.getId();
		this.image = chat.getImage();
		this.name = chat.getName();
		this.type = chat.getType();
		
		if (lastMessageAndAt != null) {
			this.lastMessage = lastMessageAndAt.getLastMessage();
			this.lastAt = lastMessageAndAt.getLastAt();
		}
		
		this.unreadCnt = unreadCnt;
		this.members = members;
	}

	public void update(LastMessageAndAt lastMessageAndAt, int unreadCnt, List<ChatUserVo> members,ChatRepo chatRepo,int chatId) {
			if(lastMessageAndAt!=null) {
				this.lastMessage = lastMessageAndAt.getLastMessage();
				this.lastAt = lastMessageAndAt.getLastAt();
			}else {
				this.lastAt =chatRepo.selectCreateTime(chatId);
			}
			this.unreadCnt = unreadCnt;
			this.members = members;
	}

	@Override
	public int compareTo(ChatVo o) {
		return Long.compare(o.lastAt, this.lastAt);
	}

}
