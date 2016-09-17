/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.ErrorEnum;
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
import org.dlut.mycloudserver.service.connpool.ISingleHostConnPool;
import org.dlut.mycloudserver.service.connpool.simpleconnpool.SimpleMutilHostConnPool;
import org.dlut.mycloudserver.service.hostmanage.HostManage;
import org.dlut.mycloudserver.service.network.NetworkService;
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

    private static Logger log = LoggerFactory.getLogger(VmListener.class);

    @Resource(name = "hostManageService")
    private IHostManageService hostManageService;

    @Resource(name = "vmManageService")
    private IVmManageService vmManageService;

    @Resource(name = "mutilHostConnPool")
    private IMutilHostConnPool mutilHostConnPool;

    /**
     * 每隔一段时间检测illegal running虚拟机
     */
    public void executeKill() {
        try {
            List<HostDTO> hostList = getOnlineHostList();
            for (HostDTO host : hostList) {
                killIllegalVmOfOneHost(host.getHostId());
            }
        }catch(java.lang.NullPointerException e){}
    }

    public void executeClose() {
        try {
            List<HostDTO> hostList = getOnlineHostList();
            for (HostDTO host : hostList) {
                closeVmOfOneHost(host.getHostId());
            }
        }catch(java.lang.NullPointerException e){}
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
                try{
                    if(conn.destroyVm(activeVmName))
                        log.info("shutdown  the vm --" + activeVmName + " running on " + hostId);
                }catch(Exception e){}
                MyCloudResult<VmDTO> result = this.vmManageService.getVmByUuid(activeVmName);
                if (result.isSuccess()) {
                    VmDTO vmDTO=result.getModel();
                    vmDTO.setVmStatus(VmStatusEnum.CLOSED);
                    this.vmManageService.updateVm(vmDTO);
                }
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
                MyCloudResult<VmDTO> result = vmManageService.getVmByUuid(activeVmName);
                if (!result.isSuccess()) {
                    log.error("获取虚拟机" + activeVmName + "失败，原因：" + result.getMsgInfo());
                } else {
                    VmDTO vmDTO = result.getModel();

                    log.info("检测到虚拟机" + vmDTO.getVmUuid() + "--" + vmDTO.getVmName() + "关机");
                    //从网关上删除虚拟机地址映射
                    String ips = vmDTO.getShowPort(); //外网IP;内网IP
                    if (ips.split(";").length != 2)
                        log.error(ErrorEnum.VM_SHOWPORT_ILLEGAL.getErrDesc());
                    else {
                        String pub_port = ips.split(";")[0].split(":")[1];
                        String pri_ipport = ips.split(";")[1];
                        String result1 = NetworkService.addOrMinusMapping("-1", pri_ipport, pub_port);
                        if ("0".equals(result1))
                            log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
                        log.info("cancle gateway mapping for " + pub_port + "--" + pri_ipport);
                    }

                    vmDTO.setVmStatus(VmStatusEnum.CLOSED);
                    vmDTO.setLastHostId(vmDTO.getHostId());
                    vmDTO.setHostId(0);
                    vmDTO.setShowPort(0 + "");
                    vmDTO.setImageUuid(0 + "");
                    MyCloudResult<Boolean> updateResult = vmManageService.updateVm(vmDTO);
                    if (!updateResult.isSuccess()) {
                        log.error("更新虚拟机" + vmDTO + "失败，原因：" + updateResult.getMsgInfo());
                    }

                }
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
        Set<Integer> hostIds = SimpleMutilHostConnPool.getRemoteMutilHostConnPoolMap().keySet();
        List<HostDTO> hostDTOs = new ArrayList<HostDTO>();
        for (int hostId : hostIds) {
            MyCloudResult<HostDTO> result = hostManageService.getHostById(hostId);
            if (!result.isSuccess()) {
                log.error("在数据库中获取物理机失败，原因：" + result.getMsgInfo());
            } else
                hostDTOs.add(result.getModel());
        }
        return hostDTOs;
    }
}
