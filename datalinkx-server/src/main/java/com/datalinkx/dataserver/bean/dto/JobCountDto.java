package com.datalinkx.dataserver.bean.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobCountDto {
    private String date;
    private String jobId;
    private Long success;
    private Long failed;
}