package com.datalinkx.dataserver.bean.vo;

import com.datalinkx.dataserver.bean.domain.DsBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DsVo extends DsBean{
    private Integer status;//数据源状态 0-未连接 1-连接成功 2-连接失败
}
