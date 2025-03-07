package com.datalinkx.deepseek.controller;

import com.datalinkx.common.result.WebResult;
import com.datalinkx.deepseek.bean.Conversation;
import com.datalinkx.deepseek.bean.Message;
import com.datalinkx.deepseek.client.request.ChatReq;
import com.datalinkx.deepseek.client.response.DeepSeekResponse;
import com.datalinkx.deepseek.service.DeepSeekService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/deepseek")
@Api(tags = "deepseek")
public class DeepSeekController {
    @Autowired
    private DeepSeekService deepSeekService;

    @PostMapping("/chat")
    public WebResult<DeepSeekResponse> chat(@RequestParam String model, @RequestBody List<ChatReq.Content> contents) {
        return WebResult.of(deepSeekService.chat(model, contents));
    }

    @GetMapping("/stream/chat")
    public SseEmitter streamChat(@RequestParam String model, @RequestParam String content, @RequestParam(required = false) String conversationId, @RequestParam Long userId) {
        log.info("stream chat");
        return deepSeekService.streamChat(model, content, conversationId, userId);
    }

    @GetMapping("/messages/history")
    public WebResult<List<Message>> getHistoryMessages(@RequestParam String conversationId) {
        return WebResult.of(deepSeekService.getHistoryMessages(conversationId));
    }

    @GetMapping("/conversions/history")
    public WebResult<List<Conversation>> getHistoryConversations(@RequestParam Long userId) {
        return WebResult.of(deepSeekService.getHistoryConversations(userId));
    }

}
