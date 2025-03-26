package com.datalinkx.deepseek.controller;

import com.datalinkx.common.result.WebResult;
import com.datalinkx.common.utils.ObjectUtils;
import com.datalinkx.deepseek.bean.domain.ConversationBean;
import com.datalinkx.deepseek.bean.domain.MessageBean;
import com.datalinkx.deepseek.bean.dto.ChatApiCountDTO;
import com.datalinkx.deepseek.bean.dto.ChatTokenCountDTO;
import com.datalinkx.deepseek.model.EventRequest;
import com.datalinkx.deepseek.repository.ConversationRepository;
import com.datalinkx.deepseek.service.DeepSeekService;
import com.datalinkx.sse.config.SseEmitterServer;
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

    @PostMapping("/stream/chat")
    public WebResult<String> streamChat(@RequestParam String model, @RequestBody EventRequest eventRequest) {
        return WebResult.of(deepSeekService.streamChat(model, eventRequest.getContent(), eventRequest.getConversationId(), eventRequest.getUserId()));
    }

    @GetMapping("/get/stream/chat")
    public SseEmitter streamChat(@RequestParam String conversationId, @RequestParam Long userId) {
        return SseEmitterServer.connect(conversationId + "-" + userId);
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

    @GetMapping("/dashboard/api/count")
    public WebResult<List<ChatApiCountDTO>> getChatApiCount(@RequestParam(defaultValue = "deepseek-chat") String model, @RequestParam(required = false) String dateFrom, @RequestParam(required = false) String dateTo) {
        return WebResult.of(deepSeekService.getChatApiCount(model,dateFrom,dateTo));
    }

    @GetMapping("/dashboard/token/count")
    public WebResult<List<ChatTokenCountDTO>> getChatTokenCount(@RequestParam(defaultValue = "deepseek-chat") String model, @RequestParam(required = false) String dateFrom, @RequestParam(required = false) String dateTo) {
        return WebResult.of(deepSeekService.getChatTokenCount(model,dateFrom,dateTo));
    }

}
