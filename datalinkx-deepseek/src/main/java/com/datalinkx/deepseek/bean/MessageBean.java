package com.datalinkx.deepseek.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "deepseek_message")
@Data
@FieldNameConstants
@SuperBuilder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
public class MessageBean {
    @Id
    private String id;
    @Column(name = "conversation_id")

    private String conversationId;
    @Column(name = "role")
    private String role;  // "user" or "assistant"
    @Column(name = "content", columnDefinition = "text comment '内容'")
    private String content;
    @Column(name = "reasoning_content", columnDefinition = "text comment '推理模型所产生的思维链'")
    private String reasoningContent;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "completion_tokens", columnDefinition = "int default 0 comment '模型 completion 产生的 token 数'")
    private int completionTokens; // 模型 completion 产生的 token 数。
    @Column(name = "prompt_tokens", columnDefinition = "int default 0 comment '用户 prompt 所包含的 token 数'")
    private int promptTokens; // 用户 prompt 所包含的 token 数。该值等于 promptCacheHitTokens + promptCacheMissTokens
    @Column(name = "prompt_cache_hit_tokens", columnDefinition = "int default 0 comment '用户 prompt 中，命中上下文缓存的 token 数'")
    private int promptCacheHitTokens; // 用户 prompt 中，命中上下文缓存的 token 数。
    @Column(name = "prompt_cache_miss_tokens", columnDefinition = "int default 0 comment '用户 prompt 中，未命中上下文缓存的 token 数'")
    private int promptCacheMissTokens; // 用户 prompt 中，未命中上下文缓存的 token 数。
    @Column(name = "total_tokens", columnDefinition = "int default 0 comment '该请求中，所有 token 的数量（prompt + completion）'")
    private int totalTokens; // 该请求中，所有 token 的数量（prompt + completion）。
    @Column(name = "reasoning_tokens", columnDefinition = "int default 0 comment '推理模型所产生的思维链 token 数量'")
    private int reasoningTokens; // 推理模型所产生的思维链 token 数量
    @Column(name = "model", columnDefinition = "varchar(255) comment '模型名称'")
    private String model; // 模型名称
    @Column(name = "is_del", columnDefinition = "int default 0 comment '是否删除'")
    private Integer isDel;
}
