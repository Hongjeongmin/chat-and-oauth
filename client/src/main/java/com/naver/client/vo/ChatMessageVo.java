package com.naver.client.vo;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatMessageVo {
    String type;
    int id;
    int userId;
    String content;
    long sentAt;
    int unreadCnt;
}
