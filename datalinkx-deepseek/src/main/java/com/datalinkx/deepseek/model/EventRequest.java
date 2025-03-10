package com.datalinkx.deepseek.model;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {
    private String content;
    private Long userId;
    private String conversationId;

    // 省略 getter 和 setter
}