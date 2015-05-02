/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.performancemonitor;

import org.dlut.mycloudserver.client.common.BaseDTO;

/**
 * 类PerformanceMonitorDTO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午4:03:17
 */
public class PerformanceMonitorDTO extends BaseDTO {

    private static final long            serialVersionUID = 3386980283044459731L;

    /**
     * 主键
     */
    private Integer                      id;

    /**
     * 别名
     */
    private String                       aliaseName;

    /**
     * 需要监听的主机的ip
     */
    private String                       ip;

    /**
     * 主机运行状态
     */
    private PerformanceMonitorStatusEnum performanceMonitorStatus;

    /**
     * 总的核心数
     */
    private Integer                      cores;

    /**
     * 总的内存大小，单位为MB
     */
    private Integer                      totalMem;

    /**
     * 已使用的内存，单位为MB
     */
    private Integer                      usedMem;

    /**
     * 平均负载值，已5分钟的为标准
     */
    private Double                       loadAverage;

    /**
     * 网卡发送速率，单位为KB/s
     */
    private Double                       sendRate;

    /**
     * 网卡的接收速率，单位为KB/s
     */
    private Double                       receiveRate;

    /**
     * 用户空间的cpu使用率
     */
    private Double                       userUsage;

    /**
     * 内核空间的cpu使用率
     */
    private Double                       systemUsage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAliaseName() {
        return aliaseName;
    }

    public void setAliaseName(String aliaseName) {
        this.aliaseName = aliaseName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public PerformanceMonitorStatusEnum getPerformanceMonitorStatus() {
        return performanceMonitorStatus;
    }

    public void setPerformanceMonitorStatus(PerformanceMonitorStatusEnum performanceMonitorStatus) {
        this.performanceMonitorStatus = performanceMonitorStatus;
    }

    public Integer getCores() {
        return cores;
    }

    public void setCores(Integer cores) {
        this.cores = cores;
    }

    public Integer getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(Integer totalMem) {
        this.totalMem = totalMem;
    }

    public Integer getUsedMem() {
        return usedMem;
    }

    public void setUsedMem(Integer usedMem) {
        this.usedMem = usedMem;
    }

    public Double getSendRate() {
        return sendRate;
    }

    public void setSendRate(Double sendRate) {
        this.sendRate = sendRate;
    }

    public Double getReceiveRate() {
        return receiveRate;
    }

    public void setReceiveRate(Double receiveRate) {
        this.receiveRate = receiveRate;
    }

    public Double getUserUsage() {
        return userUsage;
    }

    public void setUserUsage(Double userUsage) {
        this.userUsage = userUsage;
    }

    public Double getSystemUsage() {
        return systemUsage;
    }

    public void setSystemUsage(Double systemUsage) {
        this.systemUsage = systemUsage;
    }

    public Double getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(Double loadAverage) {
        this.loadAverage = loadAverage;
    }

}
