/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.performancemonitor.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO;
import org.dlut.mycloudserver.client.common.performancemonitor.QueryPerformanceMonitorCondition;
import org.dlut.mycloudserver.client.service.performancemonitor.IPerformanceMonitorService;
import org.dlut.mycloudserver.dal.dataobject.PerformanceMonitorDO;
import org.dlut.mycloudserver.service.performancemonitor.PerformanceMonitor;
import org.dlut.mycloudserver.service.performancemonitor.convent.PerformanceMonitorConvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类PerformanceMonitorServiceImpl.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午4:20:59
 */
@Service("performanceMonitorService")
public class PerformanceMonitorServiceImpl implements IPerformanceMonitorService {

    private static Logger      log = LoggerFactory.getLogger(PerformanceMonitorServiceImpl.class);

    @Resource
    private PerformanceMonitor performanceMonitor;

    @Override
    public MyCloudResult<PerformanceMonitorDTO> getPerformanceMonitorById(int id) {
        if (id <= 0) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        PerformanceMonitorDO performanceMonitorDO = performanceMonitor.getPerformanceMonitorById(id);
        PerformanceMonitorDTO performanceMonitorDTO = PerformanceMonitorConvent
                .conventToPerformanceMonitorDTO(performanceMonitorDO);
        if (performanceMonitorDTO == null) {
            log.warn("性能监控id：" + id + " 不存在");
            return MyCloudResult.failedResult(ErrorEnum.PERFORMANCE_NOT_EXIST);
        }

        return MyCloudResult.successResult(performanceMonitorDTO);
    }

    /**
     * 必须设置：aliaseName，ip，interfaceName，sshUserName，sshPassword
     */
    @Override
    public MyCloudResult<Integer> createPerformanceMonitor(PerformanceMonitorDTO performanceMonitorDTO) {
        if (performanceMonitorDTO == null || StringUtils.isBlank(performanceMonitorDTO.getAliaseName())
                || StringUtils.isBlank(performanceMonitorDTO.getIp())
                || StringUtils.isBlank(performanceMonitorDTO.getInterfaceName())
                || StringUtils.isBlank(performanceMonitorDTO.getSshUserName())
                || StringUtils.isBlank(performanceMonitorDTO.getSshPassword())) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }

        PerformanceMonitorDO performanceMonitorDO = PerformanceMonitorConvent
                .conventToPerformanceMonitorDO(performanceMonitorDTO);

        int newPerformanceMonitorId = performanceMonitor.createPerformanceMonitor(performanceMonitorDO);
        if (newPerformanceMonitorId == 0) {
            return MyCloudResult.failedResult(ErrorEnum.PERFORMANCE_CREATE_FAIL);
        }
        return MyCloudResult.successResult(newPerformanceMonitorId);
    }

    /**
     * 不能更新ip和id
     */
    @Override
    public MyCloudResult<Boolean> updatePerformanceMonitor(PerformanceMonitorDTO performanceMonitorDTO) {
        if (performanceMonitorDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        PerformanceMonitorDO performanceMonitorDO = PerformanceMonitorConvent
                .conventToPerformanceMonitorDO(performanceMonitorDTO);
        if (!performanceMonitor.updatePerformanceMonitor(performanceMonitorDO)) {
            return MyCloudResult.failedResult(ErrorEnum.PERFORMANCE_UPDATE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deletePerformanceMonitor(int id) {
        if (id <= 0) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        if (!performanceMonitor.deletePerformanceMonitor(id)) {
            return MyCloudResult.failedResult(ErrorEnum.PERFORMANCE_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Integer> countQuery(QueryPerformanceMonitorCondition queryPerformanceMonitorCondition) {
        if (queryPerformanceMonitorCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = performanceMonitor.countQuery(queryPerformanceMonitorCondition);
        return MyCloudResult.successResult(totalCount);
    }

    @Override
    public MyCloudResult<Pagination<PerformanceMonitorDTO>> query(QueryPerformanceMonitorCondition queryPerformanceMonitorCondition) {
        if (queryPerformanceMonitorCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = performanceMonitor.countQuery(queryPerformanceMonitorCondition);
        List<PerformanceMonitorDO> performanceMonitorDOList = performanceMonitor
                .query(queryPerformanceMonitorCondition);
        Pagination<PerformanceMonitorDTO> pagination = new Pagination<PerformanceMonitorDTO>(
                queryPerformanceMonitorCondition.getOffset(), queryPerformanceMonitorCondition.getLimit(), totalCount,
                PerformanceMonitorConvent.conventToPerformanceMonitorDTOList(performanceMonitorDOList));
        return MyCloudResult.successResult(pagination);
    }

}
