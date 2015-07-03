/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.performancemonitor.convent;

import java.util.ArrayList;
import java.util.List;

import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorStatusEnum;
import org.dlut.mycloudserver.dal.dataobject.PerformanceMonitorDO;

/**
 * 类PerformanceMonitorConvent.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午4:23:17
 */
public class PerformanceMonitorConvent {

    public static PerformanceMonitorDTO conventToPerformanceMonitorDTO(PerformanceMonitorDO performanceMonitorDO) {
        if (performanceMonitorDO == null) {
            return null;
        }

        PerformanceMonitorDTO performanceMonitorDTO = new PerformanceMonitorDTO();
        performanceMonitorDTO.setAliaseName(performanceMonitorDO.getAliaseName());
        performanceMonitorDTO.setCores(performanceMonitorDO.getCores());
        performanceMonitorDTO.setId(performanceMonitorDO.getId());
        performanceMonitorDTO.setIp(performanceMonitorDO.getIp());
        performanceMonitorDTO.setLoadAverage(performanceMonitorDO.getLoadAverage());
        performanceMonitorDTO.setPerformanceMonitorStatus(PerformanceMonitorStatusEnum
                .getPerformanceMonitorStatusByStatus(performanceMonitorDO.getPerformanceMonitorStatus()));
        performanceMonitorDTO.setReceiveRate(performanceMonitorDO.getReceiveRate());
        performanceMonitorDTO.setSendRate(performanceMonitorDO.getSendRate());
        performanceMonitorDTO.setSystemUsage(performanceMonitorDO.getSystemUsage());
        performanceMonitorDTO.setTotalMem(performanceMonitorDO.getTotalMem());
        performanceMonitorDTO.setUsedMem(performanceMonitorDO.getUsedMem());
        performanceMonitorDTO.setUserUsage(performanceMonitorDO.getUserUsage());

        return performanceMonitorDTO;
    }

    public static PerformanceMonitorDO conventToPerformanceMonitorDO(PerformanceMonitorDTO performanceMonitorDTO) {
        if (performanceMonitorDTO == null) {
            return null;
        }

        PerformanceMonitorDO performanceMonitorDO = new PerformanceMonitorDO();
        performanceMonitorDO.setAliaseName(performanceMonitorDTO.getAliaseName());
        performanceMonitorDO.setCores(performanceMonitorDTO.getCores());
        performanceMonitorDO.setId(performanceMonitorDTO.getId());
        performanceMonitorDO.setIp(performanceMonitorDTO.getIp());
        performanceMonitorDO.setLoadAverage(performanceMonitorDTO.getLoadAverage());
        performanceMonitorDO.setReceiveRate(performanceMonitorDTO.getReceiveRate());
        performanceMonitorDO.setSendRate(performanceMonitorDTO.getSendRate());
        if (performanceMonitorDTO.getPerformanceMonitorStatus() != null) {
            performanceMonitorDO.setPerformanceMonitorStatus(performanceMonitorDTO.getPerformanceMonitorStatus()
                    .getStatus());
        }
        performanceMonitorDO.setSystemUsage(performanceMonitorDTO.getSystemUsage());
        performanceMonitorDO.setTotalMem(performanceMonitorDTO.getTotalMem());
        performanceMonitorDO.setUsedMem(performanceMonitorDTO.getUsedMem());
        performanceMonitorDO.setUserUsage(performanceMonitorDTO.getUserUsage());

        return performanceMonitorDO;
    }

    public static List<PerformanceMonitorDTO> conventToPerformanceMonitorDTOList(List<PerformanceMonitorDO> performanceMonitorDOList) {
        List<PerformanceMonitorDTO> performanceMonitorDTOList = new ArrayList<PerformanceMonitorDTO>();
        if (performanceMonitorDOList == null) {
            return performanceMonitorDTOList;
        }
        for (PerformanceMonitorDO performanceMonitorDO : performanceMonitorDOList) {
            performanceMonitorDTOList.add(conventToPerformanceMonitorDTO(performanceMonitorDO));
        }

        return performanceMonitorDTOList;
    }
}
