/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.schedule;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类RandomScheduler.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月2日 下午8:19:15
 */
public class RandomScheduler implements IScheduler {

    private static Logger      log = LoggerFactory.getLogger(RandomScheduler.class);

    @Resource(name = "hostManageService")
    private IHostManageService hostManageService;

    @Override
    public Integer getBestHostId(VmDTO vmDTO) {
        QueryHostCondition queryHostCondition = new QueryHostCondition();
        queryHostCondition.setHostStatusEnum(HostStatusEnum.RUNNING);
        queryHostCondition.setLimit(100);
        queryHostCondition.setOffset(0);
        MyCloudResult<Pagination<HostDTO>> result = hostManageService.query(queryHostCondition);
        if (!result.isSuccess()) {
            log.error("获取运行中的物理机列表失败，原因：" + result.getMsgInfo());
            return null;
        }
        List<HostDTO> hostList = result.getModel().getList();
        if (hostList.isEmpty()) {
            log.warn("没有正在运行的物理机");
            return null;
        }
        Random random = new Random();
        return hostList.get(Math.abs(random.nextInt() % hostList.size())).getHostId();
    }
}
