package com.datalinkx.deepseek.controller;

import com.datalinkx.common.result.WebResult;
import com.datalinkx.common.utils.ObjectUtils;
import com.datalinkx.deepseek.bean.ConversationBean;
import com.datalinkx.deepseek.bean.MessageBean;
import com.datalinkx.deepseek.client.request.ChatReq;
import com.datalinkx.deepseek.client.response.DeepSeekResponse;
import com.datalinkx.deepseek.repository.ConversationRepository;
import com.datalinkx.deepseek.service.DeepSeekService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private ConversationRepository conversationRepository;

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
    public WebResult<List<MessageBean>> getHistoryMessages(@RequestParam String conversationId) {
        return WebResult.of(deepSeekService.getHistoryMessages(conversationId));
    }

    @GetMapping("/conversations/history")
    public WebResult<List<ConversationBean>> getHistoryConversations(@RequestParam Long userId) {
        return WebResult.of(deepSeekService.getHistoryConversations(userId));
    }

    @DeleteMapping("/conversations/delete")
    public WebResult<String> deleteConversation(@RequestParam String conversationId) {
        conversationRepository.deleteById(conversationId);
        return WebResult.of(conversationId);
    }

    @PutMapping("/conversations/update")
    public WebResult<ConversationBean> updateConversation(@RequestBody ConversationBean conversationBean) {
        conversationRepository.findById(conversationBean.getId()).ifPresent(conversation -> {
            BeanUtils.copyProperties(conversationBean, conversation, ObjectUtils.getNullPropertyNames(conversationBean));
            conversationRepository.save(conversation);
        });
        return WebResult.of(conversationBean);
    }
}
