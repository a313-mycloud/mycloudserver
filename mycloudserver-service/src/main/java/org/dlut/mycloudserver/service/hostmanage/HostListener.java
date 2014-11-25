/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.hostmanage;

import java.util.List;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Host监听器，用于每隔一段时间检测物理机是否可用
 * 
 * @author luojie 2014年11月25日 下午2:56:22
 */
@Service
public class HostListener {

    private static Logger      log = LoggerFactory.getLogger(HostListener.class);

    @Resource(name = "hostManageService")
    private IHostManageService hostManageService;

    public void execute() {
        QueryHostCondition queryHostCondition = new QueryHostCondition();
        queryHostCondition.setLimit(100);
        queryHostCondition.setOffset(0);
        MyCloudResult<Pagination<HostDTO>> result = hostManageService.query(queryHostCondition);
        if (!result.isSuccess()) {
            log.error("获取物理机列表失败，原因：" + result.getMsgInfo());
            return;
        }
        List<HostDTO> hostList = result.getModel().getList();
        for (HostDTO hostDTO : hostList) {
            HostStatusEnum hostStatusEnum = testConnection(hostDTO.getHostIp());
            if (hostDTO.getHostStatusEnum() != hostStatusEnum) {
                hostDTO.setHostStatusEnum(hostStatusEnum);
                MyCloudResult<Boolean> updateResult = hostManageService.updateHost(hostDTO);
                if (!updateResult.isSuccess()) {
                    log.error("更新物理机状态失败，原因：" + updateResult.getMsgInfo());
                }
            }
        }
    }

    /**
     * 获取物理机运行状态
     * 
     * @param ip
     * @return
     */
    private HostStatusEnum testConnection(String ip) {
        Connect conn = null;
        try {
            conn = new Connect("qemu+tcp://" + ip + "/system");
            if (conn.isConnected()) {
                return HostStatusEnum.RUNNING;
            }
            return HostStatusEnum.CLOSED;
        } catch (LibvirtException e) {
            log.warn("物理机 " + ip + " 连接失败，原因：" + e.getMessage());
            return HostStatusEnum.CLOSED;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (LibvirtException e) {
                    log.error("error message", e);
                }
            }
        }
    }
}
