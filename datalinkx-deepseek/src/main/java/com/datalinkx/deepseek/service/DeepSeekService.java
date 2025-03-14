package com.datalinkx.deepseek.service;


import com.datalinkx.deepseek.bean.ConversationBean;
import com.datalinkx.deepseek.bean.MessageBean;

import java.util.List;

public interface DeepSeekService {
    String streamChat(String model, String content, String conversationId, Long userId);

    List<MessageBean> getHistoryMessages(String conversationId);

    List<ConversationBean> getHistoryConversations(Long userId);
}
