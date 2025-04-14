package com.datalinkx.visualization.bean.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "update datalinkx.user_chart_images set is_del = 1 where id = ?")
@Table(name = "user_chart_images", schema = "datalinkx")
public class UserChartImageBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull
    @Column(name = "image", nullable = false)
    private byte[] image;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @Column(name = "is_del", columnDefinition = "int default 0 comment '是否删除'")
    private Integer isDel;

}