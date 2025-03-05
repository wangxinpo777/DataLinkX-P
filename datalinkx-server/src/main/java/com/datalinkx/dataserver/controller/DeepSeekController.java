package com.datalinkx.dataserver.controller;

import com.datalinkx.common.result.WebResult;
import com.datalinkx.copilot.client.request.ChatReq;
import com.datalinkx.copilot.client.response.DeepSeekResponse;
import com.datalinkx.dataserver.service.DeepSeekService;
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
    public SseEmitter streamChat(@RequestParam String model, @RequestParam String content) {
        log.info("stream chat");
        return deepSeekService.streamChat(model, content);
    }
}
