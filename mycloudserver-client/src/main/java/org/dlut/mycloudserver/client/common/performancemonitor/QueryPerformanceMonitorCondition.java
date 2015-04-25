/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.performancemonitor;

import java.io.Serializable;

/**
 * 类QueryPerformanceMonitorCondition.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午3:26:52
 */
public class QueryPerformanceMonitorCondition implements Serializable {

    private static final long            serialVersionUID = -3317843727972713588L;

    /**
     * 主键
     */
    private Integer                      id;

    /**
     * 待监视的主机的运行状态
     */
    private PerformanceMonitorStatusEnum performanceMonitorStatus;

    private int                          offset           = 0;

    private int                          limit            = 10;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PerformanceMonitorStatusEnum getPerformanceMonitorStatus() {
        return performanceMonitorStatus;
    }

    public void setPerformanceMonitorStatus(PerformanceMonitorStatusEnum performanceMonitorStatus) {
        this.performanceMonitorStatus = performanceMonitorStatus;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
