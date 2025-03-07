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
@Table(name = "message")
@Data
@FieldNameConstants
@SuperBuilder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    private String id;
    @Column(name = "conversation_id")

    private String conversationId;
    @Column(name = "role")
    private String role;  // "user" or "assistant"
    @Column(name = "content")
    private String content;
    @Column(name = "reasoning_content")
    private String reasoningContent;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "is_del")
    private Integer isDel;
}
