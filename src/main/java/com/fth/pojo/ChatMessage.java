package com.fth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String from;   // 发送者用户名
    private String to;     // 接收者用户名
    private String content; // 消息内容
}