package com.datalinkx.visualization.bean.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveChartImageRequest {

    private Integer userId;
    private Integer imageId;
    private String image; // Base64 图片数据
    private String description;
    private String chartConfig;
    private String chartJsonData;
    private Integer type; // 0 系统生成 1 用户上传
}