package com.datalinkx.deepseek.service;


import com.datalinkx.deepseek.bean.ConversationBean;
import com.datalinkx.deepseek.bean.MessageBean;
import com.datalinkx.deepseek.client.request.ChatReq;
import com.datalinkx.deepseek.client.response.DeepSeekResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface DeepSeekService {
    DeepSeekResponse chat(String model, List<ChatReq.Content> contents);

    SseEmitter streamChat(String model, String content, String conversationId, Long userId);

    List<MessageBean> getHistoryMessages(String conversationId);

    List<ConversationBean> getHistoryConversations(Long userId);
}
