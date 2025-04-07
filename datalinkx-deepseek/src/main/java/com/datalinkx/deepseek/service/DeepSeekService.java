package com.datalinkx.deepseek.service;


import com.datalinkx.deepseek.bean.domain.ConversationBean;
import com.datalinkx.deepseek.bean.domain.MessageBean;
import com.datalinkx.deepseek.bean.dto.ChatApiCountDTO;
import com.datalinkx.deepseek.bean.dto.ChatTokenCountDTO;
import com.datalinkx.deepseek.client.request.ChatReq;
import com.datalinkx.deepseek.client.response.DeepSeekResponse;

import java.util.List;

public interface DeepSeekService {

    DeepSeekResponse chat(String model, List<ChatReq.Content> contents);

    String streamChat(String model, String content, String conversationId, Long userId);

    List<MessageBean> getHistoryMessages(String conversationId);

    List<ConversationBean> getHistoryConversations(Long userId);

    List<ChatApiCountDTO> getChatApiCount(String model, String dateFrom, String dateTo);

    List<ChatTokenCountDTO> getChatTokenCount(String model,String dateFrom,String dateTo);

    DeepSeekResponse getErrorAnalysis(String model, String content);

    DeepSeekResponse getSystemAnalysis(String model, String content);
}
