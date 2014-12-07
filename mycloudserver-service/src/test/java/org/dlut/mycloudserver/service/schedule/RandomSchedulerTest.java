/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.schedule;

import javax.annotation.Resource;

import org.dlut.mycloudserver.service.BaseTestCase;
import org.junit.Test;

/**
 * 类RandomSchedulerTest.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月2日 下午9:08:19
 */
public class RandomSchedulerTest extends BaseTestCase {

    @Resource(name = "scheduler")
    private IScheduler scheduler;

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.schedule.RandomScheduler#getBestHostId(org.dlut.mycloudserver.client.common.vmmanage.VmDTO)}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetBestHostId() throws InterruptedException {
        Thread.sleep(15000);
        Integer hostId = scheduler.getBestHostId(null);
        printObject(hostId);
    }

}
