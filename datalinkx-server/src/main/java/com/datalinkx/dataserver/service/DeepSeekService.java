package com.datalinkx.dataserver.service;

import com.datalinkx.copilot.client.request.ChatReq;
import com.datalinkx.copilot.client.response.DeepSeekResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface DeepSeekService {
    DeepSeekResponse chat(String model,  List<ChatReq.Content> contents);

    SseEmitter streamChat(String model, String content);
}
