package com.datalinkx.visualization.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChartImageVo {
    private Integer id;
    private Integer userId;
    private String image; // Base64 图片数据
    private String description;
    private Timestamp createdTime;
    private Integer type; // 0 系统生成 1 用户上传
}
