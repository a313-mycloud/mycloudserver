/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.dlut.mycloudserver.client.common.vmmanage.SystemTypeEnum;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmStatusEnum;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.IMutilHostConnPool;
import org.dlut.mycloudserver.service.hostmanage.HostManage;
import org.libvirt.LibvirtException;
import org.mycloudserver.common.constants.StoreConstants;
import org.mycloudserver.common.util.CommonUtil;
import org.mycloudserver.common.util.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

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
     * 每隔一段时间检测虚拟机
     */
    public void execute() {
        List<HostDTO> hostList = getOnlineHostList();
        for (HostDTO host : hostList) {
            checkVmOfOneHost(host.getHostId());
        }
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

    /**
     * 检测一台物理机中的虚拟机
     * 
     * @param hostId
     * @throws LibvirtException
     */
    private void checkVmOfOneHost(int hostId) {
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
            if (runningVmUuidFromDB.contains(activeVmName)) {
                runningVmUuidFromDB.remove(activeVmName);
            } else {
                setVmIsRunning(activeVmName, hostId);
            }
        }

        for (String vmUuid : runningVmUuidFromDB) {
            if (this.vmManage.getVmByUuid(vmUuid).getVmStatus() == VmStatusEnum.RUNNING.getStatus())
                setVmIsClose(vmUuid);
        }
    }

    /**
     * 在数据库中设置虚拟机为运行状态
     * 
     * @param hostId
     */
    private void setVmIsRunning(String vmUuid, int hostId) {
        //        Connection conn = mutilHostConnPool.getConnByHostId(hostId);
        //        if (conn == null) {
        //            log.error("从连接池中获取连接失败");
        //            return;
        //        }
        //        String vmDescXml = null;
        //        try {
        //            Domain domain = conn.getDomainByName(vmUuid);
        //            vmDescXml = domain.getXMLDesc(0);
        //        } catch (LibvirtException e) {
        //            log.error("error message", e);
        //            return;
        //        } finally {
        //            try {
        //                conn.close();
        //            } catch (LibvirtException e) {
        //                log.error("error message", e);
        //            }
        //        }

        MyCloudResult<VmDTO> result = vmManageService.getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.error("获取虚拟机" + vmUuid + "失败，原因：" + result.getMsgInfo());
        } else if (result.getModel() == null) {
            log.error("the vm-" + vmUuid + "   active in " + hostId + " host machine exists not in database");
        } else {
            VmDTO vmDTO = result.getModel();
            vmDTO.setVmStatus(VmStatusEnum.RUNNING);
            vmDTO.setHostId(hostId);

            //从DHCP获取虚拟机的IP地址
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("macAddress", vmDTO.getVmMacAddress());
            String result1 = "";
            try {
                result1 = HttpRequest.post(StoreConstants.GETIPBYSERVERSERVER, params);
            } catch (Exception e) {
                log.error(ErrorEnum.VM_DHCP_FAIL.getErrDesc());
            }
            JSONObject json = JSONObject.parseObject(result1);
            String isSuccess = json.getString("isSuccess");
            if ("0".equals(isSuccess))
                log.error(ErrorEnum.VM_DHCP_FAIL.getErrDesc());
            else {
                //在网关上做虚拟机地址映射
                params.clear();
                String pri_ipport = "";
                if (vmDTO.getSystemType().getValue() == SystemTypeEnum.WINDOWS.getValue())
                    pri_ipport = json.getString("ip") + ":3389";
                else
                    pri_ipport = json.getString("ip") + ":22";
                params.put("pri_ipport", pri_ipport);
                params.put("action", "1");
                params.put("pub_port", "");
                try {
                    result1 = HttpRequest.post(StoreConstants.DOMAPPINGSERVER, params);
                } catch (Exception e) {
                    log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
                }
                json = JSONObject.parseObject(result1);
                isSuccess = json.getString("isSuccess");
                if ("0".equals(isSuccess))
                    log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
                if ("0".equals(this.vmManage.getVmByUuid(vmUuid))) {
                    vmDTO.setShowPort(json.getString("port") + ";" + pri_ipport);
                    //        vmDTO.setShowPort(CommonUtil.getShowPortFromVmDescXml(vmDescXml)+"");

                    MyCloudResult<Boolean> updateResult = vmManageService.updateVm(vmDTO);
                    if (!updateResult.isSuccess()) {
                        log.error("更新虚拟机" + vmDTO + "失败，原因：" + updateResult.getMsgInfo());
                    }
                } else {
                    params.put("action", "-1");
                    try {
                        result1 = HttpRequest.post(StoreConstants.DOMAPPINGSERVER, params);
                    } catch (Exception e) {
                        log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
                    }
                    json = JSONObject.parseObject(result1);
                    isSuccess = json.getString("isSuccess");
                    if ("0".equals(isSuccess))
                        log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
                }
            }
        }
    }

    private void setVmIsClose(String vmUuid) {
        MyCloudResult<VmDTO> result = vmManageService.getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.error("获取虚拟机" + vmUuid + "失败，原因：" + result.getMsgInfo());
        }
        VmDTO vmDTO = result.getModel();
        String lastHostIp = this.hostManage.getHostById(vmDTO.getHostId()).getHostIp();

        //从网关上删除虚拟机地址映射
        String ips = vmDTO.getShowPort(); //外网IP;内网IP
        if (ips.split(";").length != 2)
            log.error(ErrorEnum.VM_SHOWPORT_ILLEGAL.getErrDesc());
        else {
            String pub_port = ips.split(";")[0].split(":")[1];
            String pri_ipport = ips.split(";")[1];
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("pub_port", pub_port);
            params.put("pri_ipport", pri_ipport);
            params.put("action", "-1");
            String result1 = "";
            try {
                result1 = HttpRequest.post(StoreConstants.DOMAPPINGSERVER, params);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
            }
            JSONObject json = JSONObject.parseObject(result1);
            if ("0".equals(json.getString("isSuccess")))
                log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
            log.info("cancle gateway mapping for " + ips);
        }

        vmDTO.setVmStatus(VmStatusEnum.CLOSED);
        vmDTO.setLastHostId(vmDTO.getHostId());
        vmDTO.setHostId(0);
        vmDTO.setShowPort(0 + "");
        MyCloudResult<Boolean> updateResult = vmManageService.updateVm(vmDTO);
        if (!updateResult.isSuccess()) {
            log.error("更新虚拟机" + vmDTO + "失败，原因：" + updateResult.getMsgInfo());
        }
        /**
         * 开启异步线程，将镜像上传到主机 上传完毕后，版本号加1，文件重新设置为可读
         */
        /************* start **************************************/
        executorService.submit(new CopyImageFromHostTask(vmUuid, lastHostIp, this.vmManage));
        /************** end *************************************/
        log.info("检测到虚拟机" + vmDTO.getVmUuid() + "--" + vmDTO.getVmName() + "关机");
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
        queryVmCondition.setLimit(100);
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

}
