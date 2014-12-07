/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.schedule;

import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;

/**
 * 类IScheduler.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月2日 下午8:12:38
 */
public interface IScheduler {

    /**
     * 根据需要运行的虚拟机获取最佳物理机
     * 
     * @param vmDTO
     * @return
     */
    public Integer getBestHostId(VmDTO vmDTO);
}
