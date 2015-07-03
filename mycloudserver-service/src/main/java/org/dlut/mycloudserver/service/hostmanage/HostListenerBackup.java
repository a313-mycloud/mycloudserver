/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.hostmanage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
public class HostListenerBackup implements Runnable {

    private static Logger      log                            = LoggerFactory.getLogger(HostListenerBackup.class);

    /**
     * 设置检测的周期，单位为ms
     */
    private static final int   CYCLE_TIME                     = 5000;

    /**
     * 测试连接的超时时间，单位s
     */
    private static final int   TEST_TIME_OUT                  = 3;

    @Resource(name = "hostManageService")
    private IHostManageService hostManageService;

    /**
     * 保存正在进行连接测试的物理节点的id
     */
    private Set<Integer>       runningTestConnectionHostIdSet = new HashSet<Integer>();

    /**
     * 用来执行物理机节点连接测试的线程
     */
    private ExecutorService    executorService                = Executors.newCachedThreadPool();

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
            Future<HostDTO> future = null;
            synchronized (runningTestConnectionHostIdSet) {
                if (!runningTestConnectionHostIdSet.contains(hostDTO.getHostId())) {
                    future = executorService.submit(new TestOneHostConnectionTask(hostDTO));
                    runningTestConnectionHostIdSet.add(hostDTO.getHostId());
                }
            }
            // 如果当前处于运行状态，则需要快速判断是否断开连接
            if (hostDTO.getHostStatusEnum() == HostStatusEnum.RUNNING && future != null) {
                try {
                    future.get(TEST_TIME_OUT, TimeUnit.SECONDS);
                } catch (Exception e) {
                    // 超时
                    log.warn("futer.get() 物理机 " + hostDTO.getHostIp() + " 连接失败，原因：" + e);
                    updateHostStatusToDB(hostDTO.getHostId(), HostStatusEnum.CLOSED);
                }
            }
        }
    }

    private void setAllHostClosed() {
        List<HostDTO> hostList = getHostListFromDB();
        for (HostDTO hostDTO : hostList) {
            updateHostStatusToDB(hostDTO.getHostId(), HostStatusEnum.CLOSED);
        }
    }

    /**
     * 对一个物理机节点进行连接测试
     * 
     * @author luojie 2015年4月30日 下午5:53:45
     */
    private class TestOneHostConnectionTask implements Callable<HostDTO> {

        private HostDTO hostDTO;

        public TestOneHostConnectionTask(HostDTO hostDTO) {
            this.hostDTO = hostDTO;
        }

        @Override
        public HostDTO call() throws Exception {
            Connect conn = null;
            HostStatusEnum hostStatusEnum = hostDTO.getHostStatusEnum();
            try {
                conn = new Connect("qemu+tcp://" + hostDTO.getHostIp() + "/system");
                if (conn.isConnected()) {
                    hostStatusEnum = HostStatusEnum.RUNNING;
                } else {
                    hostStatusEnum = HostStatusEnum.CLOSED;
                }
            } catch (Exception e) {
                hostStatusEnum = HostStatusEnum.CLOSED;
                log.warn("物理机 " + hostDTO.getHostIp() + " 连接失败，原因：" + e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (LibvirtException e) {
                        log.error("error message", e);
                    }
                }
            }

            if (hostDTO.getHostStatusEnum() != hostStatusEnum) {
                updateHostStatusToDB(hostDTO.getHostId(), hostStatusEnum);
            }

            HostDTO resHostDTO = new HostDTO();
            resHostDTO.setHostId(hostDTO.getHostId());
            resHostDTO.setHostStatusEnum(hostStatusEnum);

            synchronized (runningTestConnectionHostIdSet) {
                runningTestConnectionHostIdSet.remove(hostDTO.getHostId());
            }

            return resHostDTO;
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
