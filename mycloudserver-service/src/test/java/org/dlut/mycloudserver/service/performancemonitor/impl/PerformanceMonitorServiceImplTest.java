/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.performancemonitor.impl;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorStatusEnum;
import org.dlut.mycloudserver.client.service.performancemonitor.IPerformanceMonitorService;
import org.dlut.mycloudserver.service.BaseTestCase;
import org.dlut.mycloudserver.service.performancemonitor.PerformanceListener;
import org.junit.Test;

/**
 * 类PerformanceMonitorServiceImplTest.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午6:34:25
 */
public class PerformanceMonitorServiceImplTest extends BaseTestCase {

    @Resource(name = "performanceMonitorService")
    private IPerformanceMonitorService performanceMonitorService;

    @Resource
    private PerformanceListener        performanceListener;

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.performancemonitor.impl.PerformanceMonitorServiceImpl#getPerformanceMonitorById(int)}
     * .
     */
    @Test
    public void testGetPerformanceMonitorById() {
        MyCloudResult<PerformanceMonitorDTO> res = performanceMonitorService.getPerformanceMonitorById(2);
        printObject(res);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.performancemonitor.impl.PerformanceMonitorServiceImpl#createPerformanceMonitor(org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO)}
     * .
     */
    @Test
    public void testCreatePerformanceMonitor() {
        PerformanceMonitorDTO performanceMonitorDTO = new PerformanceMonitorDTO();
        performanceMonitorDTO.setAliaseName("web");
        performanceMonitorDTO.setInterfaceName("eth0");
        performanceMonitorDTO.setIp("192.168.0.82");
        performanceMonitorDTO.setSshUserName("luojie");
        performanceMonitorDTO.setSshPassword("10041104");
        MyCloudResult<Integer> res = performanceMonitorService.createPerformanceMonitor(performanceMonitorDTO);
        printObject(res);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.performancemonitor.impl.PerformanceMonitorServiceImpl#updatePerformanceMonitor(org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO)}
     * .
     */
    @Test
    public void testUpdatePerformanceMonitor() {
        PerformanceMonitorDTO performanceMonitorDTO = new PerformanceMonitorDTO();
        performanceMonitorDTO.setId(1);
        performanceMonitorDTO.setCores(3);
        performanceMonitorDTO.setLoadAverage(3.45);
        performanceMonitorDTO.setPerformanceMonitorStatus(PerformanceMonitorStatusEnum.RUNNING);
        performanceMonitorDTO.setReceiveRate(456.34);
        performanceMonitorDTO.setSendRate(345.36);
        performanceMonitorDTO.setUserUsage(4.3);
        performanceMonitorDTO.setSystemUsage(5.6);
        MyCloudResult<Boolean> res = performanceMonitorService.updatePerformanceMonitor(performanceMonitorDTO);
        printObject(res);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.performancemonitor.impl.PerformanceMonitorServiceImpl#deletePerformanceMonitor(int)}
     * .
     */
    @Test
    public void testDeletePerformanceMonitor() {
        MyCloudResult<Boolean> res = performanceMonitorService.deletePerformanceMonitor(2);
        printObject(res);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.performancemonitor.impl.PerformanceMonitorServiceImpl#countQuery(org.dlut.mycloudserver.client.common.performancemonitor.QueryPerformanceMonitorCondition)}
     * .
     */
    @Test
    public void testCountQuery() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.performancemonitor.impl.PerformanceMonitorServiceImpl#query(org.dlut.mycloudserver.client.common.performancemonitor.QueryPerformanceMonitorCondition)}
     * .
     */
    @Test
    public void testQuery() {
        fail("Not yet implemented");
    }

    @Test
    public void testPerformanceMonitorListener() {
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
