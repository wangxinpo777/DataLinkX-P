package com.datalinkx.deepseek.bean.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "deepseek_conversation")
@Data
@FieldNameConstants
@SuperBuilder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update deepseek_conversation set is_del = 1 where id = ?")
public class ConversationBean {
    @Id
    private String id;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "title")
    private String title;
    @Column(name = "created_time")
    private Timestamp createdTime;
    @Column(name = "is_del", columnDefinition = "int default 0 comment '是否删除'")
    private Integer isDel;
}
