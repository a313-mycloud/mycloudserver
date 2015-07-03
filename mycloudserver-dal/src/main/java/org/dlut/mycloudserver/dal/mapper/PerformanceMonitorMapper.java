/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.dlut.mycloudserver.client.common.performancemonitor.QueryPerformanceMonitorCondition;
import org.dlut.mycloudserver.dal.dataobject.PerformanceMonitorDO;

/**
 * 类PerformanceMonitorMapper.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午2:50:42
 */
public interface PerformanceMonitorMapper {

    PerformanceMonitorDO getPerformanceMonitorById(@Param("id") int id);

    PerformanceMonitorDO getPerformanceMonitorByIp(String ip);

    int createPerformanceMonitor(PerformanceMonitorDO performanceMonitorDO);

    int updatePerformanceMonitor(PerformanceMonitorDO performanceMonitorDO);

    int deletePerformanceMonitor(@Param("id") int id);

    int deletePerformanceMonitorByIp(String ip);

    int countQuery(QueryPerformanceMonitorCondition queryPerformanceMonitorCondition);

    List<PerformanceMonitorDO> query(QueryPerformanceMonitorCondition queryPerformanceMonitorCondition);
}
