package com.datalinkx.deepseek.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatApiCountDTO {
    private String date;

    private String model;
    private Long count;
}
