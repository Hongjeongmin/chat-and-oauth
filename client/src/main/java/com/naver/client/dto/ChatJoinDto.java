package com.naver.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatJoinDto {
	int chatId;
	int[] invitedIds;
}
