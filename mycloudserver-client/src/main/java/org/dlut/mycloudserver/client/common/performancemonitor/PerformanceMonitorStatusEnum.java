/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.performancemonitor;

/**
 * 类PerformanceMonitorStatusEnum.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午3:29:06
 */
public enum PerformanceMonitorStatusEnum {

    CLOSED(0, "关闭中"),
    RUNNING(1, "运行中"),
    ERROR(2, "错误");

    private int    status;
    private String desc;

    public static PerformanceMonitorStatusEnum getPerformanceMonitorStatusByStatus(int status) {
        for (PerformanceMonitorStatusEnum performanceMonitorStatusEnum : PerformanceMonitorStatusEnum.values()) {
            if (performanceMonitorStatusEnum.getStatus() == status) {
                return performanceMonitorStatusEnum;
            }
        }

        return null;
    }

    private PerformanceMonitorStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
