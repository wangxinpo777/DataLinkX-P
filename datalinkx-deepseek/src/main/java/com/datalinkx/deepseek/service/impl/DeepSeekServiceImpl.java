package com.datalinkx.deepseek.service.impl;

import com.datalinkx.common.utils.JsonUtils;
import com.datalinkx.deepseek.bean.Conversation;
import com.datalinkx.deepseek.bean.Message;
import com.datalinkx.deepseek.client.DeepSeekClient;
import com.datalinkx.deepseek.client.request.ChatReq;
import com.datalinkx.deepseek.client.response.DeepSeekResponse;
import com.datalinkx.deepseek.repository.ConversationRepository;
import com.datalinkx.deepseek.repository.MessageRepository;
import com.datalinkx.deepseek.service.DeepSeekService;
import com.datalinkx.sse.config.SseEmitterServer;
import com.datalinkx.sse.config.oksse.RealEventSource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeepSeekServiceImpl implements DeepSeekService {
    @Value("${deepseek.model}")
    private String model;
    @Value("${deepseek.api_key}")
    private String apiKey;
    @Value("${deepseek.system_content}")
    private String systemContent;

    @Value("${client.deepseek.url}")
    private String deepseekUrl;

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Override
    public DeepSeekResponse chat(String model, List<ChatReq.Content> contents) {
        // 检查是否是第一次调用（即 contents 仅包含用户的第一条消息）
        contents = getContents(contents);
        ChatReq chatReq = ChatReq.builder()
                .messages(contents)
                .stream(false)
                .model(StringUtils.isNotEmpty(model) ? model : this.model)
                .build();
        return deepSeekClient.chat(chatReq, "Bearer " + apiKey);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SseEmitter streamChat(String model, String content, String conversationId, Long userId) {
        ChatReq.Content chatContent = ChatReq.Content.builder()
                .role("user")
                .content(content)
                .build();
        if (StringUtils.isEmpty(conversationId)) {
            conversationId = UUID.randomUUID().toString().replaceAll("-", "");
            // 保存会话
            saveConversation(userId, conversationId, content.substring(0, Math.min(content.length(), 10)));
        }

        List<Message> historyMessages = getHistoryMessages(conversationId);
        List<ChatReq.Content> contents = new ArrayList<ChatReq.Content>() {{
            addAll(historyMessages.stream()
                    .map(message -> ChatReq.Content.builder()
                            .role(message.getRole())
                            .content(message.getContent())
                            .build())
                    .collect(Collectors.toList()));
            add(chatContent);
        }};
        // 检查是否是第一次调用（即 contents 仅包含用户的第一条消息）
        contents = getContents(contents);
        ChatReq chatReq = ChatReq.builder()
                .messages(contents)
                .stream(true)
                .model(StringUtils.isNotEmpty(model) ? model : this.model)
                .build();
        String bodyString = JsonUtils.toJson(chatReq);
        String largeModelUrl = deepseekUrl + "/chat/completions";
        Request.Builder okRequestBuilder = new Request.Builder().url(largeModelUrl);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, bodyString);
        okRequestBuilder.header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json").header("Accept", "application/json"); // 确保接收 JSON 格式的响应
        okRequestBuilder.post(requestBody);
        return transformRequest(okRequestBuilder.build(), UUID.randomUUID().toString().replaceAll("-", ""), conversationId, chatContent);
    }

    @Override
    public List<Message> getHistoryMessages(String conversationId) {
        return messageRepository.findByConversationId(conversationId)
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public List<Conversation> getHistoryConversations(Long userId) {
        return conversationRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Conversation::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    private List<ChatReq.Content> getContents(List<ChatReq.Content> contents) {
        if (!CollectionUtils.isEmpty(contents) && contents.stream().noneMatch(c -> "system".equals(c.getRole()))) {
            List<ChatReq.Content> newContents = new ArrayList<>();
            // 添加 system 角色的消息
            newContents.add(ChatReq.Content.builder()
                    .role("system")
                    .content(this.systemContent)
                    .build());

            // 添加用户的消息（第一次传来的内容）
            newContents.addAll(contents);

            // 使用新的消息列表
            contents = newContents;
        }
        return contents;
    }

    public void saveMessage(String conversationId, String role, String content, String reasoningContent) {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setReasoningContent(reasoningContent);
        message.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        messageRepository.save(message);
    }

    public void saveConversation(Long userId, String conversationId, String title) {
        Conversation conversation = new Conversation();
        conversation.setTitle(title);
        conversation.setId(conversationId);
        conversation.setUserId(userId);
        conversation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        conversationRepository.save(conversation);
    }

    public SseEmitter transformRequest(Request request, String connectId, String conversationId, ChatReq.Content chatContent) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.DAYS)
                .readTimeout(1, TimeUnit.DAYS) //这边需要将超时显示设置长一点，不然刚连上就断开，之前以为调用方式错误被坑了半天
                .build();

        SseEmitter sseEmitter = SseEmitterServer.connect(connectId);
        // 存放 AI 逐步推送的内容
        StringBuilder aiResponse = new StringBuilder();
        StringBuilder aiResponseTemp = new StringBuilder();
        // 实例化EventSource，注册EventSource监听器
        RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {
            final CopyOnWriteArrayList<DeepSeekResponse> deepSeekResponses = new CopyOnWriteArrayList<>();

            @Override
            public void onOpen(EventSource eventSource, Response response) {
                // 先保存用户输入
                saveMessage(conversationId, chatContent.getRole(), chatContent.getContent(), "");
                log.info("SSE open");
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                String[] dataList = Arrays.stream(data.split("\n"))
                        .map(dataItem -> dataItem.startsWith("data: ") ? dataItem.substring(6) : dataItem)
                        .toArray(String[]::new);
                log.info("SSE data: {}", Arrays.toString(dataList));
                for (String dataItem : dataList) {
                    if (dataItem.startsWith("{") && dataItem.endsWith("}")) {
                        DeepSeekResponse deepSeekResponse = JsonUtils.toObject(dataItem, DeepSeekResponse.class);
                        String content = deepSeekResponse.getChoices().get(0).getDelta().getContent();
                        String reasoningContent = deepSeekResponse.getChoices().get(0).getDelta().getReasoning_content();
                        if (StringUtils.isNotEmpty(content)) {
                            aiResponse.append(content);
                        }
                        if (StringUtils.isNotEmpty(reasoningContent)) {
                            aiResponseTemp.append(reasoningContent);
                        }
                        deepSeekResponses.add(deepSeekResponse);

                        try {
                            SseEmitter.SseEventBuilder event = SseEmitter.event()
                                    .data(deepSeekResponse)
                                    .id(deepSeekResponse.getId())
                                    .name(type);
                            sseEmitter.send(event);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                            sseEmitter.completeWithError(e);
                        }
                    }
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("SSE close");
                saveMessage(conversationId, "assistant", aiResponse.toString(), aiResponseTemp.toString());
                sseEmitter.complete();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                saveMessage(conversationId, "assistant", aiResponse.toString(), aiResponseTemp.toString());
                if (response != null) {
                    String msg;
                    try {
                        msg = response.body().string();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    if (!org.springframework.util.StringUtils.hasLength(msg)) {
                        msg = response.message();
                    }
                    log.error(msg);
                }
                sseEmitter.completeWithError(t);
            }
        });

        realEventSource.connect(okHttpClient); // 真正开始请求的一步
        // 在SSE连接关闭时执行清理操作
        sseEmitter.onCompletion(realEventSource::cancel);

        // 处理连接错误事件
        sseEmitter.onError(error ->

        {
            // 在发生错误时执行处理
            log.error("SSE web err: " + error.toString());
            realEventSource.cancel();
        });
        return sseEmitter;
    }

}
