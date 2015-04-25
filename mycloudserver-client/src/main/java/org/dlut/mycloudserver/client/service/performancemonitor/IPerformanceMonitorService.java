/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.service.performancemonitor;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO;
import org.dlut.mycloudserver.client.common.performancemonitor.QueryPerformanceMonitorCondition;

/**
 * 类IPerformanceMonitorService.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午4:02:12
 */
public interface IPerformanceMonitorService {

    /**
     * 根据id获取一个主机的性能监控信息
     * 
     * @param id
     * @return
     */
    public MyCloudResult<PerformanceMonitorDTO> getPerformanceMonitorById(int id);

    /**
     * 创建一个对某个主机的性能监控 必须设置：aliaseName，ip，interfaceName
     * 
     * @param performanceMonitorDTO
     * @return
     */
    public MyCloudResult<Integer> createPerformanceMonitor(PerformanceMonitorDTO performanceMonitorDTO);

    /**
     * 更新一个主机的性能监控，不能更新ip和id
     * 
     * @param performanceMonitorDTO
     * @return
     */
    public MyCloudResult<Boolean> updatePerformanceMonitor(PerformanceMonitorDTO performanceMonitorDTO);

    /**
     * 删除对一个主机的性能监控
     * 
     * @param performanceMonitorDTO
     * @return
     */
    public MyCloudResult<Boolean> deletePerformanceMonitor(int id);

    /**
     * 根据添加查询数量
     * 
     * @param queryPerformanceMonitorCondition
     * @return
     */
    public MyCloudResult<Integer> countQuery(QueryPerformanceMonitorCondition queryPerformanceMonitorCondition);

    /**
     * 根据条件查询列表
     * 
     * @param queryPerformanceMonitorCondition
     * @return
     */
    public MyCloudResult<Pagination<PerformanceMonitorDTO>> query(QueryPerformanceMonitorCondition queryPerformanceMonitorCondition);

}
