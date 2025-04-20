package com.datalinkx.dataserver.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitStatsVo {
    private String date;
    private String count;
}
