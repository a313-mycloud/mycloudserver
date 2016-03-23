/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmStatusEnum;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.IMutilHostConnPool;
import org.dlut.mycloudserver.service.hostmanage.HostManage;
import org.libvirt.LibvirtException;
import org.mycloudserver.common.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类VmListener.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月13日 下午6:29:59
 */
@Service
public class VmListener {

    private static Logger      log             = LoggerFactory.getLogger(VmListener.class);

    @Resource(name = "hostManageService")
    private IHostManageService hostManageService;

    @Resource(name = "vmManageService")
    private IVmManageService   vmManageService;

    @Resource(name = "vmManage")
    private VmManage           vmManage;

    @Resource(name = "hostManage")
    private HostManage         hostManage;
    @Resource(name = "mutilHostConnPool")
    private IMutilHostConnPool mutilHostConnPool;

    private ExecutorService    executorService = Executors.newCachedThreadPool();

    /**
     * 每隔一段时间检测illegal running虚拟机
     */
    public void executeKill() {
        List<HostDTO> hostList = getOnlineHostList();
        for (HostDTO host : hostList) {
            killIllegalVmOfOneHost(host.getHostId());
        }
    }

    public void executeClose() {
        List<HostDTO> hostList = getOnlineHostList();
        for (HostDTO host : hostList) {
            closeVmOfOneHost(host.getHostId());
        }
    }

    /**
     * 检测一台物理机中的虚拟机
     * 
     * @param hostId
     * @throws LibvirtException
     */
    private void killIllegalVmOfOneHost(int hostId) {
        Connection conn = mutilHostConnPool.getConnByHostId(hostId);
        if (conn == null) {
            log.error("从连接池中获取连接失败");
            return;
        }
        List<String> activeVmNameList;
        try {
            activeVmNameList = conn.listActiveVmName();
        } catch (LibvirtException e) {
            log.error("error message", e);
            return;
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }
        Set<String> activeVmNameSet = filterVmNameList(activeVmNameList);
        Set<String> runningVmUuidFromDB = getRunningVmListInOneHostFromDB(hostId);

        for (String activeVmName : activeVmNameSet) {
            if (!runningVmUuidFromDB.contains(activeVmName)) {
                executorService.execute(new KillVMTask(activeVmName, vmManageService, hostId));
            }
        }

    }

    private void closeVmOfOneHost(int hostId) {
        Connection conn = mutilHostConnPool.getConnByHostId(hostId);
        if (conn == null) {
            log.error("从连接池中获取连接失败");
            return;
        }
        List<String> activeVmNameList;
        try {
            activeVmNameList = conn.listActiveVmName();
        } catch (LibvirtException e) {
            log.error("error message", e);
            return;
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }
        Set<String> activeVmNameSet = filterVmNameList(activeVmNameList);
        Set<String> runningVmUuidFromDB = getRunningVmListInOneHostFromDB(hostId);

        for (String activeVmName : runningVmUuidFromDB) {
            if (!activeVmNameSet.contains(activeVmName)) {
                executorService.execute(new SetVMCloseTask(activeVmName, vmManageService, hostManage));
            }
        }
    }

    /**
     * 根据数据库的数据获取某个指定的物理机的运行中的虚拟机uuid
     * 
     * @param hostId
     * @return
     */
    private Set<String> getRunningVmListInOneHostFromDB(int hostId) {
        Set<String> runningVmSetDB = new HashSet<String>();
        QueryVmCondition queryVmCondition = new QueryVmCondition();
        queryVmCondition.setHostId(hostId);
        queryVmCondition.setVmStatus(VmStatusEnum.RUNNING);
        queryVmCondition.setOffset(0);
        queryVmCondition.setLimit(1000);
        MyCloudResult<Pagination<VmDTO>> result = vmManageService.query(queryVmCondition);
        if (!result.isSuccess()) {
            log.error("在数据中获取物理机下的在线虚拟机列表失败，原因：" + result.getMsgInfo());
            return runningVmSetDB;
        }
        for (VmDTO vmDTO : result.getModel().getList()) {
            runningVmSetDB.add(vmDTO.getVmUuid());
        }
        return runningVmSetDB;
    }

    /**
     * 过滤那些不符合uuid的名称的虚拟机名称
     * 
     * @param vmNameList
     * @return
     */
    private Set<String> filterVmNameList(List<String> vmNameList) {
        Set<String> vmNameSet = new HashSet<String>();
        for (String vmName : vmNameList) {
            if (CommonUtil.isUuidFormat(vmName)) {
                vmNameSet.add(vmName);
            }
        }
        return vmNameSet;
    }

    private List<HostDTO> getOnlineHostList() {
        QueryHostCondition queryHostCondition = new QueryHostCondition();
        queryHostCondition.setHostStatusEnum(HostStatusEnum.RUNNING);
        queryHostCondition.setOffset(0);
        queryHostCondition.setLimit(1000);
        MyCloudResult<Pagination<HostDTO>> result = hostManageService.query(queryHostCondition);
        if (!result.isSuccess()) {
            log.error("在数据库中获取在线的物理机列表失败，原因：" + result.getMsgInfo());
            return new ArrayList<HostDTO>();
        }
        return result.getModel().getList();
    }
}
