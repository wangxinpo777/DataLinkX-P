package com.datalinkx.deepseek.service;


import com.datalinkx.deepseek.bean.domain.ConversationBean;
import com.datalinkx.deepseek.bean.domain.MessageBean;
import com.datalinkx.deepseek.bean.dto.ChatApiCountDTO;
import com.datalinkx.deepseek.bean.dto.ChatTokenCountDTO;

import java.util.List;

public interface DeepSeekService {
    String streamChat(String model, String content, String conversationId, Long userId);

    List<MessageBean> getHistoryMessages(String conversationId);

    List<ConversationBean> getHistoryConversations(Long userId);

    List<ChatApiCountDTO> getChatApiCount(String model, String dateFrom, String dateTo);

    List<ChatTokenCountDTO> getChatTokenCount(String model,String dateFrom,String dateTo);
}
