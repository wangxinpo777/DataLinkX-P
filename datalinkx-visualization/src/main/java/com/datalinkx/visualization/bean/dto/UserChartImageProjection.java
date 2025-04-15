package com.datalinkx.visualization.bean.dto;

import java.sql.Timestamp;

public interface UserChartImageProjection {
    Integer getId();
    Integer getUserId();
    byte[] getImage();
    String getDescription();
    Timestamp getCreatedTime();
    Integer getType();
}
