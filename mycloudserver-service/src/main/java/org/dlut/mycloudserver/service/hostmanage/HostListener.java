/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.hostmanage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorStatusEnum;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.dlut.mycloudserver.client.service.performancemonitor.IPerformanceMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Host监听器，用于每隔一段时间检测物理机是否可用
 * 
 * @author luojie 2014年11月25日 下午2:56:22
 */
@Service
public class HostListener implements Runnable {

    private static Logger              log        = LoggerFactory.getLogger(HostListener.class);

    /**
     * 设置检测的周期，单位为ms
     */
    private static final int           CYCLE_TIME = 2000;

    @Resource(name = "hostManageService")
    private IHostManageService         hostManageService;

    @Resource(name = "performanceMonitorService")
    private IPerformanceMonitorService performanceMonitorService;

    public void execute() {
        setAllHostClosed();
        new Thread(this).start();
    }

    @Override
    public void run() {
        log.info("启动心跳检测后台线程成功");
        while (true) {
            try {
                testAllHostConnectionOnce();
                Thread.sleep(CYCLE_TIME);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    /**
     * 对所有注册了的物理机进行一次连接测试
     */
    private void testAllHostConnectionOnce() {
        List<HostDTO> hostList = getHostListFromDB();
        for (HostDTO hostDTO : hostList) {
            HostStatusEnum hostStatusEnum = HostStatusEnum.CLOSED;
            MyCloudResult<PerformanceMonitorDTO> res = performanceMonitorService.getPerformanceMonitorByIp(hostDTO
                    .getHostIp());
            if (res.isSuccess()) {
                PerformanceMonitorDTO performanceMonitorDTO = res.getModel();
                if (performanceMonitorDTO.getPerformanceMonitorStatus() == PerformanceMonitorStatusEnum.RUNNING) {
                    hostStatusEnum = HostStatusEnum.RUNNING;
                }
            }
            if (hostStatusEnum != hostDTO.getHostStatusEnum()) {
                updateHostStatusToDB(hostDTO.getHostId(), hostStatusEnum);
            }
        }
    }

    private void setAllHostClosed() {
        List<HostDTO> hostList = getHostListFromDB();
        for (HostDTO hostDTO : hostList) {
            updateHostStatusToDB(hostDTO.getHostId(), HostStatusEnum.CLOSED);
        }
    }

    private void updateHostStatusToDB(int hostId, HostStatusEnum hostStatusEnum) {
        HostDTO hostDTO = new HostDTO();
        hostDTO.setHostId(hostId);
        hostDTO.setHostStatusEnum(hostStatusEnum);
        MyCloudResult<Boolean> updateResult = hostManageService.updateHost(hostDTO);
        if (!updateResult.isSuccess()) {
            log.error("更新物理机状态失败，原因：" + updateResult.getMsgInfo());
        }
    }

    private List<HostDTO> getHostListFromDB() {
        QueryHostCondition queryHostCondition = new QueryHostCondition();
        queryHostCondition.setLimit(100);
        queryHostCondition.setOffset(0);
        MyCloudResult<Pagination<HostDTO>> result = hostManageService.query(queryHostCondition);
        if (!result.isSuccess()) {
            log.error("获取物理机列表失败，原因：" + result.getMsgInfo());
            return new ArrayList<HostDTO>();
        }
        return result.getModel().getList();
    }

}
