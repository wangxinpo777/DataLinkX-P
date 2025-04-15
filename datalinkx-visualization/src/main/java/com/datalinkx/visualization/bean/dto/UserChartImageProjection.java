package com.datalinkx.visualization.bean.dto;

import java.sql.Timestamp;

public interface UserChartImageProjection {
    Integer getId();
    Integer getUserId();
    String getUserName();
    byte[] getImage();
    String getDescription();
    Timestamp getCreatedTime();
    Timestamp getUpdatedTime();
    Integer getType();
}
