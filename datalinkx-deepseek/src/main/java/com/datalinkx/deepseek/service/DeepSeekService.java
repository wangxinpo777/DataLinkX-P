package com.datalinkx.deepseek.service;


import com.datalinkx.deepseek.bean.ConversationBean;
import com.datalinkx.deepseek.bean.MessageBean;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface DeepSeekService {
    SseEmitter streamChat(String model, String content, String conversationId, Long userId);

    List<MessageBean> getHistoryMessages(String conversationId);

    List<ConversationBean> getHistoryConversations(Long userId);
}
