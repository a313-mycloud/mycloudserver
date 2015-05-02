/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.hostmanage.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO;
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmStatusEnum;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.dlut.mycloudserver.client.service.performancemonitor.IPerformanceMonitorService;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.dal.dataobject.HostDO;
import org.dlut.mycloudserver.service.hostmanage.HostManage;
import org.dlut.mycloudserver.service.hostmanage.convent.HostConvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类HostManageServiceImpl.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年10月29日 下午11:12:25
 */
@Service("hostManageService")
public class HostManageServiceImpl implements IHostManageService {

    private static Logger              log = LoggerFactory.getLogger(HostManageServiceImpl.class);

    @Resource
    private HostManage                 hostManage;

    @Resource(name = "vmManageService")
    private IVmManageService           vmManageService;

    @Resource(name = "performanceMonitorService")
    private IPerformanceMonitorService performanceMonitorService;

    /**
     * 根据id获取主机
     */
    @Override
    public MyCloudResult<HostDTO> getHostById(int hostId) {
        HostDO hostDO = hostManage.getHostById(hostId);
        return MyCloudResult.successResult(HostConvent.conventToHostDTO(hostDO));
    }

    /**
     * 创建主机, hostName和hostIp必填
     */
    @Override
    public MyCloudResult<Integer> createHost(HostDTO hostDTO) {
        HostDO hostDO = HostConvent.conventToHostDO(hostDTO);
        if (hostDO == null || StringUtils.isBlank(hostDO.getHostName()) || StringUtils.isBlank(hostDO.getHostIp())) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }

        // 检测是否对该主机注册了心跳检测，如果没有，则注册
        MyCloudResult<PerformanceMonitorDTO> res = performanceMonitorService.getPerformanceMonitorByIp(hostDTO
                .getHostIp());
        if (!res.isSuccess()) {
            PerformanceMonitorDTO performanceMonitorDTO = new PerformanceMonitorDTO();
            performanceMonitorDTO.setAliaseName(hostDO.getHostName());
            performanceMonitorDTO.setIp(hostDTO.getHostIp());
            MyCloudResult<Integer> createRes = performanceMonitorService
                    .createPerformanceMonitor(performanceMonitorDTO);
            if (!createRes.isSuccess()) {
                return MyCloudResult.failedResult(createRes.getMsgCode(), createRes.getMsgInfo());
            }
        }

        return MyCloudResult.successResult(hostManage.createHost(hostDO));
    }

    /**
     * hostId必填
     */
    @Override
    public MyCloudResult<Boolean> updateHost(HostDTO hostDTO) {
        HostDO hostDO = HostConvent.conventToHostDO(hostDTO);
        if (hostDO == null || hostDO.getHostId() == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        return MyCloudResult.successResult(hostManage.updateHost(hostDO));
    }

    @Override
    public MyCloudResult<Boolean> deleteHostById(int hostId) {
        if (hostId <= 0) {
            MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        MyCloudResult<HostDTO> hostResult = getHostById(hostId);
        if (!hostResult.isSuccess()) {
            return MyCloudResult.failedResult(ErrorEnum.HOST_NOT_EXIST);
        }
        // 如果物理机处于运行状态，则强制关闭运行在此物理机上的虚拟机
        if (hostResult.getModel().getHostStatusEnum() == HostStatusEnum.RUNNING) {
            // 获取在该物理机上运行的虚拟机列表
            QueryVmCondition queryVmCondition = new QueryVmCondition();
            queryVmCondition.setHostId(hostId);
            queryVmCondition.setVmStatus(VmStatusEnum.RUNNING);
            queryVmCondition.setOffset(0);
            queryVmCondition.setLimit(100);
            MyCloudResult<Pagination<VmDTO>> result = vmManageService.query(queryVmCondition);
            if (!result.isSuccess()) {
                log.error("获取物理机下运行的虚拟机列表失败，原因：" + result.getMsgInfo());
                return MyCloudResult.failedResult(result.getMsgCode(), result.getMsgInfo());
            }
            for (VmDTO vmDTO : result.getModel().getList()) {
                MyCloudResult<Boolean> forceResult = vmManageService.forceShutDownVm(vmDTO.getVmUuid());
                if (!forceResult.isSuccess()) {
                    return MyCloudResult.failedResult(forceResult.getMsgCode(), forceResult.getMsgInfo());
                }
            }
        }

        if (!hostManage.deleteHostById(hostId)) {
            return MyCloudResult.failedResult(ErrorEnum.HOST_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Integer> countQuery(QueryHostCondition queryHostCondition) {
        if (queryHostCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        return MyCloudResult.successResult(hostManage.countQuery(queryHostCondition));
    }

    @Override
    public MyCloudResult<Pagination<HostDTO>> query(QueryHostCondition queryHostCondition) {
        if (queryHostCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = hostManage.countQuery(queryHostCondition);
        List<HostDO> hostDOList = hostManage.query(queryHostCondition);
        Pagination<HostDTO> pagination = new Pagination<HostDTO>(queryHostCondition.getOffset(),
                queryHostCondition.getLimit(), totalCount, HostConvent.conventToHostDTOList(hostDOList));
        return MyCloudResult.successResult(pagination);
    }
}
