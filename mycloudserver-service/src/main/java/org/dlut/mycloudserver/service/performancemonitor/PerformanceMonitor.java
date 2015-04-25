/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.performancemonitor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.performancemonitor.QueryPerformanceMonitorCondition;
import org.dlut.mycloudserver.dal.dataobject.PerformanceMonitorDO;
import org.dlut.mycloudserver.dal.mapper.PerformanceMonitorMapper;
import org.springframework.stereotype.Service;

/**
 * 类PerformanceMonitor.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午3:48:22
 */
@Service
public class PerformanceMonitor {

    @Resource
    private PerformanceMonitorMapper performanceMonitorMapper;

    public PerformanceMonitorDO getPerformanceMonitorById(int id) {
        if (id <= 0) {
            return null;
        }

        return performanceMonitorMapper.getPerformanceMonitorById(id);
    }

    public int createPerformanceMonitor(PerformanceMonitorDO performanceMonitorDO) {
        if (performanceMonitorDO == null) {
            return 0;
        }
        int res = performanceMonitorMapper.createPerformanceMonitor(performanceMonitorDO);
        if (res == 1) {
            return performanceMonitorDO.getId();
        }
        return 0;
    }

    public boolean updatePerformanceMonitor(PerformanceMonitorDO performanceMonitorDO) {
        if (performanceMonitorDO == null) {
            return false;
        }

        return performanceMonitorMapper.updatePerformanceMonitor(performanceMonitorDO) == 1 ? true : false;
    }

    public boolean deletePerformanceMonitor(int id) {
        if (id <= 0) {
            return false;
        }

        return performanceMonitorMapper.deletePerformanceMonitor(id) == 1 ? true : false;
    }

    public int countQuery(QueryPerformanceMonitorCondition queryPerformanceMonitorCondition) {
        if (queryPerformanceMonitorCondition == null) {
            return 0;
        }
        return performanceMonitorMapper.countQuery(queryPerformanceMonitorCondition);
    }

    public List<PerformanceMonitorDO> query(QueryPerformanceMonitorCondition queryPerformanceMonitorCondition) {
        if (queryPerformanceMonitorCondition == null) {
            return new ArrayList<PerformanceMonitorDO>();
        }

        return performanceMonitorMapper.query(queryPerformanceMonitorCondition);
    }
}
