package com.naver.client.vo;

import com.naver.client.redis.RedisChatRoomRepo;

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
    
    /*
     * 
     */

}
