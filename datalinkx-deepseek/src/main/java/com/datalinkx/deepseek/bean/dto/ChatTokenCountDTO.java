package com.datalinkx.deepseek.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatTokenCountDTO {

    private String date; // 日期
    private String model; // 模型名称
    private Long promptCacheHitTokens; // 命中缓存的 token 数量
    private Long promptCacheMissTokens; // 未命中缓存的 token 数量
    private Long promptTokens; // 用户 prompt 的 token 数量
    private Long completionTokens; // 模型 completion 的 token 数量
    private Long totalTokens; // 总 token 数量

}
